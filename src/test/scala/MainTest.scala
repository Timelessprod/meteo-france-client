import org.scalatest.funsuite.AnyFunSuite
import com.meteofrance.Api

class MainTest extends AnyFunSuite {
  test("Check user API credentials") {
    assert(Api.USER_ID != null)
    assert(Api.USER_KEY != null)
  }
}
