package cn.java666.etlflink.app

import cn.java666.etlflink.sink.MyESSinkFun
import cn.java666.etlflink.source.MyRedisSourceFun
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink
import org.apache.http.HttpHost

import scala.collection.JavaConversions._

/**
 * @author Geek
 * @date 2020-04-14 17:06:40
 *
 * redis szt:pageJson 抽取源数据到 elasticsearch-7
 * 
 * 注意，启动之前先为 szt-data 索引创建时间映射，否则看不到时时更新的kibana 图表

{
  "properties": {
    "deal_date": {
      "format": "yyyy-MM-dd HH:mm:ss",
      "type": "date"
    }
  }
}

效果：.file/.pic/es-szt-data.png
 
 */
object Redis2ES {
	def main(args: Array[String]): Unit = {
		val env = StreamExecutionEnvironment.getExecutionEnvironment
		env.setParallelism(1)
		
		val hosts = List[HttpHost](
			new HttpHost("cdh231", 9200),
			new HttpHost("cdh232", 9200),
			new HttpHost("cdh233", 9200)
		)
		
		val esSink = new ElasticsearchSink.Builder(
			hosts, new MyESSinkFun("szt-data")
		).build
		
		env.addSource(new MyRedisSourceFun)
			.addSink(esSink)
		
		env.execute("Redis2ES")
	}
}
