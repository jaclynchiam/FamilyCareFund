package storage

import models.*
import upickle.default.*

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.{Try, Using}


// ============================================================
// USER JSON RECORD
// DTO ONLY FOR JSON SERIALISATION
// ============================================================

case class UserRecord(
                       userId: String,
                       fullName: String,
                       email: String,
                       username: String,
                       password: String,
                       phoneNumber: String,
                       householdId: String
                     )

object UserRecord:

  given ReadWriter[UserRecord] = macroRW

  def fromUser(user: HouseholdUser): UserRecord =
    UserRecord(
      user.userId,
      user.fullName,
      user.email,
      user.username,
      user.password,
      user.phoneNumber,
      user.householdId
    )


// ============================================================
// FAQ JSON RECORD
// DTO ONLY FOR JSON SERIALISATION
// ============================================================

case class FAQRecord(
                      question: String,
                      answer: String
                    )

object FAQRecord:

  given ReadWriter[FAQRecord] = macroRW

  def fromFAQ(faq: FAQ): FAQRecord =
    FAQRecord(
      faq.question,
      faq.answer
    )

  def toFAQ(record: FAQRecord): FAQ =
    FAQ(
      record.question,
      record.answer
    )


// ============================================================
// FILE STORAGE
// ============================================================

object FileStorage:

  private val usersFile =
    "src/main/resources/data/users.json"

  private val householdsFile =
    "src/main/resources/data/households.json"

  private val faqsFile =
    "src/main/resources/data/faqs.json"

  private val extrasFile =
    "src/main/resources/data/extras.json"


  // ==========================================================
  // SHARED JSON WRITE HELPER
  // ==========================================================

  private def writeJson[A: Writer](
                                    path: String,
                                    data: A
                                  ): Unit =

    Try:

      val file =
        new File(path)

      val parent =
        file.getParentFile

      if parent != null && !parent.exists() then
        parent.mkdirs()

      val writer =
        new PrintWriter(file)

      try
        writer.write(
          write(data, indent = 2)
        )
      finally
        writer.close()

    .recover:

      case e: Exception =>
        println(
          s"Error writing to $path: ${e.getMessage}"
        )


  // ==========================================================
  // USERS
  // ==========================================================

  def loadUsers(): List[UserRecord] =

    val file =
      new File(usersFile)

    if !file.exists() then
      writeJson(
        usersFile,
        List.empty[UserRecord]
      )

    Try:

      val json =
        Using(
          Source.fromFile(usersFile)
        )(_.mkString)
          .getOrElse("[]")

      read[List[UserRecord]](json)

    .getOrElse:

      println(
        s"Warning: could not parse $usersFile"
      )

      List.empty


  def saveUser(
                user: HouseholdUser
              ): Unit =

    val current =
      loadUsers()

    val record =
      UserRecord.fromUser(user)

    val updated =
      current
        .filterNot(
          _.userId == record.userId
        ) :+
        record

    writeJson(
      usersFile,
      updated
    )


  def usernameExists(
                      username: String
                    ): Boolean =

    loadUsers().exists(
      _.username == username
    )


  def verifyLogin(
                   username: String,
                   password: String
                 ): Option[UserRecord] =

    loadUsers().find { user =>

      user.username == username &&
        user.password == password

    }


  // ==========================================================
  // HOUSEHOLDS
  // ==========================================================

  def loadHouseholds(): List[Household] =

    val file =
      new File(householdsFile)

    if !file.exists() then
      writeJson(
        householdsFile,
        List.empty[Household]
      )

    Try:

      val json =
        Using(
          Source.fromFile(householdsFile)
        )(_.mkString)
          .getOrElse("[]")

      read[List[Household]](json)

    .getOrElse:

      println(
        s"Warning: could not parse $householdsFile"
      )

      List.empty


  def saveHousehold(
                     household: Household
                   ): Unit =

    val current =
      loadHouseholds()

    val updated =
      current
        .filterNot(
          _.householdId == household.householdId
        ) :+
        household

    writeJson(
      householdsFile,
      updated
    )


  // ==========================================================
  // HOUSEHOLD EXTRAS (linked to HouseholdUser via username,
  // separate from Household since EligibilityRules never reads these)
  // ==========================================================

  def loadExtras(): List[HouseholdExtras] =

    val file =
      new File(extrasFile)

    if !file.exists() then
      writeJson(
        extrasFile,
        List.empty[HouseholdExtras]
      )

    Try:

      val json =
        Using(
          Source.fromFile(extrasFile)
        )(_.mkString)
          .getOrElse("[]")

      read[List[HouseholdExtras]](json)

    .getOrElse:

      println(
        s"Warning: could not parse $extrasFile"
      )

      List.empty


  def saveExtras(
                  extras: HouseholdExtras
                ): Unit =

    val current =
      loadExtras()

    val updated =
      current
        .filterNot(
          _.username == extras.username
        ) :+
        extras

    writeJson(
      extrasFile,
      updated
    )


  def findExtras(
                  username: String
                ): Option[HouseholdExtras] =

    loadExtras().find(
      _.username == username
    )


  // ==========================================================
  // FAQS
  // ==========================================================

  private val defaultFAQs : List[FAQRecord] = List(
    FAQRecord(
      "What is FamilyCareFund?",
      "FamilyCareFund is an online platform that helps low-income household check eligibility for government subsidies and financial assistance."
    ),
    FAQRecord(
      "How is my eligibility calcualted?",
      "Eligibility is automatically determined using your gross monethly household income, total number of dependents, housing status, and " +
        "property ownership."
    ),
    FAQRecord(
      "What documents do I need to prepare?",
      "You will need copies of MyKad/IDs for all household members, proof of income (pay slips or bank statements), and supporting documentations if applying for special assistance."
    ),
    FAQRecord(
      "What should I do if I need help with my application?",
      "You can submit a request under the NGO Assisstance section, and a partner representative will reach out to guide you."
    )
  )

  def loadFAQs(): List[FAQRecord] =
    val file = new File(faqsFile)

    if !file.exists() then
      writeJson(faqsFile, defaultFAQs)

    val loaded = Try {
      val json = Using(Source.fromFile(faqsFile))(_.mkString).getOrElse("[]")
      read[List[FAQRecord]](json)
    }.getOrElse {
      println(s"Warning: could not parse $faqsFile")
      List.empty[FAQRecord]
    }

    if loaded.isEmpty then
      writeJson(faqsFile, defaultFAQs)
      defaultFAQs
    else
      loaded




  // ==========================================================
  // ADD / EDIT FAQ
  // ==========================================================

  def saveFAQ(
               faq: FAQ,
               originalQuestion: Option[String] = None
             ): Unit =

    val current =
      loadFAQs()

    val targetQuestion =
      originalQuestion.getOrElse(
        faq.question
      )

    val updated =
      current
        .filterNot(
          _.question == targetQuestion
        ) :+
        FAQRecord.fromFAQ(faq)

    writeJson(
      faqsFile,
      updated
    )


  // ==========================================================
  // DELETE FAQ
  // ==========================================================

  def deleteFAQ(
                 question: String
               ): Unit =

    val current =
      loadFAQs()

    val updated =
      current.filterNot(
        _.question == question
      )

    writeJson(
      faqsFile,
      updated
    )