package ruangrupakoding.spring.helper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelperApplication

fun main(args: Array<String>) {
    HelperLogger.getLogger(HelperApplication::class.java).info("ruang rupa koding")
	runApplication<HelperApplication>(*args)
}
