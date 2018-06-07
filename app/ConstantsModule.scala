import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.api.Configuration

class ConstantsModule(configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    val refillTime = configuration.getMillis("dispenser.refillTime")
    bindConstant().annotatedWith(Names.named("dispenser.refillTime")).to(refillTime)
  }
}
