import com.meteofrance.parameters.Api
import org.scalatest.funsuite.AnyFunSuite

class MainTest extends AnyFunSuite {
  test("Check user API credentials") {
    assert(Api.USER_ID != null)
    assert(Api.USER_KEY != null)
  }
}
