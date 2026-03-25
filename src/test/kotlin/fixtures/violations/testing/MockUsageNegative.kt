package fixtures.violations.testing

class TooManyMocksTest {
    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service1: Service1

    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service2: Service2

    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service3: Service3

    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var service4: Service4
}

interface Service1
interface Service2
interface Service3
interface Service4
