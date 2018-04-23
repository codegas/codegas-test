package org.codegas.test.spock

import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(["classpath*:META-INF/spring/test-context.xml"])
abstract class IntegrationTest extends PersistenceTest {

}
