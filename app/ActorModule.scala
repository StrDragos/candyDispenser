import com.google.inject.AbstractModule
import dispenser.CandyDispenser
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorModule extends AbstractModule with AkkaGuiceSupport{
  override def configure(): Unit = {
    bindActor[CandyDispenser](CandyDispenser.name)
  }
}
