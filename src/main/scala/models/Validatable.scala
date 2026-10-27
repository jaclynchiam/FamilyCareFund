package models

// Base trait for models that validate their own internal data
trait Validatable:
  def validate(): Either[String, Unit]