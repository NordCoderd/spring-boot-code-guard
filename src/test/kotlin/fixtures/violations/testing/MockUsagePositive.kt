package fixtures.violations.testing.positive

class ReasonableMocksTest {
    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service1: Service1

    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service2: Service2
}

interface Service1
interface Service2
