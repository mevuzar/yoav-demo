package com.hamlazot.app

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.ClusterSharding
import com.hamlazot.scripts.interpreters.cqrs.AccountView


object ClusterBoot {

  def boot(proxyOnly:Boolean = false)(clusterSystem: ActorSystem):(ActorRef,ActorRef) = {(null,null)}
//    val view = ClusterSharding(clusterSystem).start(
//      typeName = AccountView.shardName,
//      entryProps = if(!proxyOnly) Some(AccountView.props()) else None,
//      idExtractor = BidView.idExtractor,
//      shardResolver = BidView.shardResolver)
//    val processor = ClusterSharding(clusterSystem).start(
//      typeName = BidProcessor.shardName,
//      entryProps = if(!proxyOnly) Some(BidProcessor.props(view)) else None,
//      idExtractor = BidProcessor.idExtractor,
//      shardResolver = BidProcessor.shardResolver)
//    (processor,view)
//  }

}
