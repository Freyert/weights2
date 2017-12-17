package controllers

import javax.inject._

import models._

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ExerciseSetController @Inject() (repo: ExerciseSetRepository,
                                       cc: MessagesControllerComponents)
                                      (implicit ec: ExecutionContext)
extends MessagesAbstractController(cc) {
  val setForm: Form[CreateExerciseSetForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "date" -> sqlDate
    )(CreateExerciseSetForm.apply)(CreateExerciseSetForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.index(setForm))
  }

  def addExerciseSet = Action.async { implicit  request =>
    setForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      exerciseSet => {
        repo.create(exerciseSet.name, exerciseSet.date).map( _ =>
          Redirect(routes.ExerciseSetController.index).flashing("success" -> "exercise set recorded")
        )
      }
    )
  }

  def getExerciseSets = Action.async { implicit request =>
    repo.list().map { exerciseSets =>
      Ok(Json.toJson(exerciseSets))
    }
  }
}

case class CreateExerciseSetForm(name: String, date: java.sql.Date )