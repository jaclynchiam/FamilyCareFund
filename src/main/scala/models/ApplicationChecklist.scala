package models

case class ApplicationChecklist(
                                 subsidy: Subsidy,
                                 documents: List[String]
                               ):

  def printChecklist(): Unit =
    println(s"=== Application Checklist for ${subsidy.name} ===")
    if documents.isEmpty then
      println("No required documents needed.")
    else
      documents.foreach(doc => println(s"[ ] $doc"))


object ApplicationChecklist:

  def generateChecklist(subsidy: Subsidy): ApplicationChecklist =
    ApplicationChecklist(
      subsidy,
      subsidy.requiredDocuments
    )