package models

case class EligibilityResult[T](
                                 item: T,
                                 eligible: Boolean,
                                 message: String
                               ):

  def getItem(): T =
    item

  def isEligible(): Boolean =
    eligible