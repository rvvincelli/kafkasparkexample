package io.github.rvvincelli.blogpost.kafkaspark

import util.Random
import java.text.ParseException
import java.util.{Map => JMap, Properties}
import argonaut._
import argonaut.Argonaut._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.Serializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import kafka.serializer.{Decoder, StringDecoder}
import java.util.{Map => JMap}
import java.util.{Map => JMap}

object KafkaSparkExample extends App with ProducerImpl with ConsumerImpl with Utils {

  //Spit out some events
  val events = Array.fill(100)(RichEvent(Random.nextString(4)))
  
  val topic = "topic"
  
  val producer = new KafkaProducer[String, RichEvent](fromMapToProperty(prodProps))
  
  events.foreach(e => producer.send(new ProducerRecord[String, RichEvent](topic, e)))
  
  //You can set many other properties here, such as the assembly jar location if your master is "yarn-client" or "yarn-cluster", the jars to distribute, memory settings for driver and executors...
  val conf = new SparkConf().setAppName("kafkasparkexample").setMaster("local[32]")
  
  val context = new SparkContext(conf)

  //Winlength is 10s, thus this is the rate at which RDDs containing data from the stream are made available
  val streamingContext = new StreamingContext(context,  Seconds(10))
  
  val consumer = KafkaUtils.createDirectStream[String, RichEvent, StringDecoder, RichEventDeserializer](streamingContext, consProps, Set(topic))
  
  consumer.print()
  consumer.count()
  
  //Long-running
  streamingContext.start()
  streamingContext.awaitTermination()
  
}

trait Utils {
  def fromMapToProperty(props: Map[String, String]) = props.foldLeft(new Properties()) { (props, pair) =>
    props.setProperty(pair._1, pair._2)
    props
  }
}

trait ArgonautSerializer[A] extends Serializer[A] {

  def encoder: EncodeJson[A]

  //Serializer trait contract - Make the value of type A a JSON string and binarize it.
  def serialize(topic: String, data: A) = encoder.encode(data).toString().getBytes

  def configure(configs: JMap[String, _], isKey: Boolean) = () // nothing to configure - we could expect the charset but let's just use the system default in the encode above
  def close() = () // nothing to close

}

trait ArgonautDecoder[A] extends Decoder[A] {

  //Contract
  def decoder: DecodeJson[A]
  
  //Read it as a String and decode it as JSON. Bytes are interpreted under the default charset.
  def fromBytes(data: Array[Byte]) = Parse.decode(new String(data))(decoder).getOrElse { throw new ParseException(s"Invalid JSON: ${new String(data)}", 0) }

}

trait Codecs {

  //As a side, Argonaut is pretty much the least boilerplate you can expect to write for JSON stuff
  protected val richEventCodec = casecodec1(RichEvent.apply, RichEvent.unapply)("content")

}

class RichEventSerializer extends ArgonautSerializer[RichEvent] with Codecs {
  def encoder = richEventCodec.Encoder
}

class RichEventDeserializer extends ArgonautDecoder[RichEvent] with Codecs {
  def decoder = richEventCodec.Decoder
}

case class RichEvent(content: String)
 
trait ProducerImpl { self: Utils =>

  val prodProps = Map[String, String](
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG      -> "kafkabroker:9092",
    ProducerConfig.ACKS_CONFIG                   -> "3",
    ProducerConfig.CLIENT_ID_CONFIG              -> "clientid",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "io.github.rvvincelli.blogpost.kafkaspark.RichEventSerializer"
  )
  
  def producer: KafkaProducer[String, RichEvent]
  
}

trait ConsumerImpl { self: Utils =>
  
  val consProps = Map[String, String](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG       -> "kafkabroker:9092",
    ConsumerConfig.GROUP_ID_CONFIG                -> "mygroup",
    ConsumerConfig.CLIENT_ID_CONFIG               -> "consumerid",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG      -> "true",
    ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "10000",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG       -> "smallest"
  )

  def streamingContext: StreamingContext
  
  def topic: String

  def consumer: InputDStream[(String, RichEvent)]
  
}
