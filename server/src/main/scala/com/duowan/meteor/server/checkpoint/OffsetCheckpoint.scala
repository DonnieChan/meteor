package com.duowan.meteor.server.checkpoint

import org.apache.spark.streaming.StreamingContext

/**
 * Created by Administrator on 2016/9/7.
 */
trait OffsetCheckpoint {
  def registerCheckpointListener(streamContext:StreamingContext):Unit
}
