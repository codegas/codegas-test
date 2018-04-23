package org.codegas.test.spock

import org.codegas.commons.domain.event.DomainEventPublisher
import spock.lang.Specification

abstract class ApplicationServiceTest extends Specification {

   def setup() {
      DomainEventPublisher.instance().reset()
   }

   def cleanup() {
      DomainEventPublisher.instance().reset()
   }
}
