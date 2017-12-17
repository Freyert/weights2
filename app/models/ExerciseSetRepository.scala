package models

import javax.inject.{ Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.ExerciseSet

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class ExerciseSetRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ExerciseSetsTable(tag: Tag) extends Table[ExerciseSet](tag, "exercisesets") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def date = column[java.sql.Date]("date")

    def * = (id, name, date) <> ((ExerciseSet.apply _).tupled, ExerciseSet.unapply)
  }

  private val exerciseSets = TableQuery[ExerciseSetsTable]

  def create(name: String, date: java.sql.Date): Future[ExerciseSet] = db.run {
    (exerciseSets.map(s => (s.name, s.date))
    returning exerciseSets.map(_.id)
    into ((nameDate, id) => ExerciseSet(id, nameDate._1, nameDate._2))
    ) += ((name, date))
  }

  def list(): Future[Seq[ExerciseSet]] = db.run {
    exerciseSets.result
  }
}
