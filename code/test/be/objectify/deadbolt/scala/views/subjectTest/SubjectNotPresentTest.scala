package be.objectify.deadbolt.scala.views.subjectTest

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.testhelpers.User
import be.objectify.deadbolt.scala.views.html.subjectTest.subjectNotPresentContent
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import play.api.mvc.{Request, Result, Results}
import play.api.test.{FakeRequest, Helpers, PlaySpecification, WithApplication}
import play.libs.Scala

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
class SubjectNotPresentTest extends PlaySpecification {

  "show constrained content when subject is not present" in new WithApplication {
    val html = subjectNotPresentContent(new DeadboltHandler() {
      override def beforeAuthCheck[A](request: Request[A]): Option[Future[Result]] = None
      override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = None
      override def getSubject[A](request: Request[A]): Option[Subject] = None
      override def onAuthFailure[A](request: Request[A]): Future[Result] = Future(Results.Forbidden)
    })(FakeRequest())

    private val content: String = Helpers.contentAsString(html)
    content must contain("This is before the constraint.")
    content must contain("This is protected by the constraint.")
    content must contain("This is after the constraint.")
  }

  "hide constrained content when subject is present" in new WithApplication {
    val html = subjectNotPresentContent(new DeadboltHandler() {
      override def beforeAuthCheck[A](request: Request[A]): Option[Future[Result]] = None
      override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = None
      override def getSubject[A](request: Request[A]): Option[Subject] = Some(new User("foo", Scala.asJava(List.empty), Scala.asJava(List.empty)))
      override def onAuthFailure[A](request: Request[A]): Future[Result] = Future(Results.Forbidden)
    })(FakeRequest())

    private val content: String = Helpers.contentAsString(html)
    content must contain("This is before the constraint.")
    content must not contain("This is protected by the constraint.")
    content must contain("This is after the constraint.")
  }
}
