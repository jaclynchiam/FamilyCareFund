import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{VBox, HBox, Priority, Region}
import scalafx.scene.control.{Button, Label, TextField, PasswordField, ListView, ComboBox, CheckBox, ScrollPane, Alert}
import scalafx.scene.control.Alert.AlertType
import scalafx.geometry.{Insets, Pos}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._

import models._
import services.AuthService
import storage.FileStorage

//Family Care Fund (Subsidy Eligible Calculator)
object Main extends JFXApp3 {

  private val WindowWidth = 720
  private val WindowHeight = 700

  // ---------------- STYLES ----------------
  // ai-assisted: #7
  // why: Used AI to implement reusable ScalaFX styles and UI helper methods.
  private val bgStyle = "-fx-background-color: #f4f6f8;"
  private val titleStyle = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #205072;"
  private val stepTitleStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #205072;"
  private val subLabelStyle = "-fx-font-size: 13px; -fx-text-fill: #55707d;"
  private val fieldStyle = "-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cfd8dc; -fx-padding: 8;"
  private val cardStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e4e7; -fx-border-radius: 8; -fx-padding: 16;"
  private val primaryButtonStyle = "-fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 16 10 16; -fx-cursor: hand;"
  private val secondaryButtonStyle = "-fx-background-color: transparent; -fx-text-fill: #205072; -fx-border-color: #205072; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 9 16 9 16; -fx-cursor: hand;"
  private val dangerButtonStyle = "-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-border-color: #e63946; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10 6 10; -fx-cursor: hand; -fx-font-size: 11px;"
  private val errorLabelStyle = "-fx-text-fill: #e63946; -fx-font-size: 12px;"
  private val eligibleStyle = "-fx-text-fill: #2a9d8f; -fx-font-weight: bold;"
  private val notEligibleStyle = "-fx-text-fill: #e63946; -fx-font-weight: bold;"
  private val navBarStyle = "-fx-background-color: #ffffff; -fx-border-color: #e0e4e7; -fx-border-width: 0 0 1 0;"
  private val navItemActiveStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2a9d8f; -fx-padding: 14 16 14 16; -fx-cursor: hand;"
  private val navItemStyle = "-fx-font-size: 13px; -fx-text-fill: #55707d; -fx-padding: 14 16 14 16; -fx-cursor: hand;"

  // Holds every household field being edited, so navigating to Add Member
  // and back doesn't lose anything the user already typed/selected.
  private class HouseholdDraft {
    var age: String = ""
    var employment: String = null
    var income: String = ""
    var dependent: Boolean = false
    var householdCount: String = "1"
    var housing: String = "Renting"
    var vehicle: String = "No"
    var additionalProperty: String = "No"
    var propertyCount: String = "0"
    var singleParent: Boolean = false
    var receivesAssistance: String = "No"
    var assistanceType: String = null
    var savings: String = "Prefer not to say"
    var disability: Boolean = false
    var care: Boolean = false
  }

  // In-progress member list while building a household (not yet saved)
  private var draftMembers: ObservableBuffer[HouseholdMember] = ObservableBuffer.empty
  private var draft: HouseholdDraft = new HouseholdDraft()
  // Tracks which household's draft is currently loaded, so re-entering the
  // form (e.g. after Add Member) doesn't wipe out not-yet-saved edits
  private var draftLoadedFor: Option[String] = None

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "FamilyCareFund"
      minWidth = WindowWidth
      minHeight = WindowHeight
      maxWidth = WindowWidth + 300
      maxHeight = WindowHeight + 300
      scene = landingScene()
    }
  }


  // ai-assisted: #7
  // why: Used AI to create reusable UI helper methods for consistent ScalaFX components.
  private def styledField(f: TextField): TextField = { f.style = fieldStyle; f }
  private def styledPassField(f: PasswordField): PasswordField = { f.style = fieldStyle; f }
  private def primaryButton(text: String)(action: => Unit): Button = new Button(text) { style = primaryButtonStyle; onAction = _ => action }
  private def secondaryButton(text: String)(action: => Unit): Button = new Button(text) { style = secondaryButtonStyle; onAction = _ => action }
  private def scrollable(c: VBox): ScrollPane = new ScrollPane { content = c; fitToWidth = true; style = "-fx-background-color: transparent;" }
  private def info(msg: String): Unit = new Alert(AlertType.Information) { contentText = msg }.showAndWait()

  private def parseIntSafe(s: String): Either[String, Int] =
    scala.util.Try(s.trim.toInt).toEither.left.map(_ => s"'$s' is not a valid whole number")
  private def parseDoubleSafe(s: String): Either[String, Double] =
    scala.util.Try(s.trim.toDouble).toEither.left.map(_ => s"'$s' is not a valid number")

  private def topNav(user: HouseholdUser, active: String): HBox = {
    def navLabel(text: String, tab: String)(action: => Unit): Label =
      new Label(text) { style = if (tab == active) navItemActiveStyle else navItemStyle; onMouseClicked = _ => action }
    val logo = new Label("FamilyCareFund") { style = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #205072; -fx-padding: 14 18 14 18;" }
    val spacer = new Region(); HBox.setHgrow(spacer, Priority.Always)
    new HBox {
      style = navBarStyle
      children = Seq(
        logo,
        navLabel("Home", "home") { stage.scene = homeScene(user) },
        navLabel("My Household", "household") { stage.scene = householdFormScene(user) },
        navLabel("Subsidies", "subsidies") { stage.scene = exploreSubsidiesScene(user) },
        navLabel("FAQ", "faq") { stage.scene = faqScene(Some(user)) },
        spacer,
        navLabel("Log out", "logout") { stage.scene = landingScene() }
      )
    }
  }

  // ai-assisted: #8
  // why: Used AI to implement the landing, login and registration ScalaFX scenes.
  // ================= LANDING =================
  def landingScene(): Scene = {
    new Scene(WindowWidth, WindowHeight) {
      root = new VBox(16) {
        style = bgStyle; padding = Insets(40); alignment = Pos.Center
        children = Seq(
          new Label("FamilyCareFund") { style = "-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #205072;" },
          new Label("Find the support your family needs") { style = subLabelStyle },
          new Label("Discover government subsidies, check your household eligibility,\nand prepare your application checklist easily.") {
            style = subLabelStyle; wrapText = true
          },
          primaryButton("Get Started") { stage.scene = loginScene() }
        )
      }
    }
  }


  // ================= LOGIN =================
  def loginScene(): Scene = {
    val userField = styledField(new TextField { promptText = "Username" })
    val passField = styledPassField(new PasswordField { promptText = "Password" })
    val messageLabel = new Label("") { style = errorLabelStyle }

    val loginButton = primaryButton("Login") {
      if (userField.text.value.trim.isEmpty || passField.text.value.trim.isEmpty) {
        messageLabel.text = "Please enter both username and password"
      } else {
        AuthService.login(userField.text.value, passField.text.value) match {
          case Some(h: HouseholdUser) => stage.scene = homeScene(h)
          case Some(_)                => stage.scene = adminDashboard()
          case None                   => messageLabel.text = "Invalid username or password"
        }
      }
    }
    val registerButton = secondaryButton("New user? Register") { stage.scene = registerScene() }
    val backButton = secondaryButton("Back") { stage.scene = landingScene() }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox(14) {
        style = bgStyle; padding = Insets(30); alignment = Pos.Center
        children = Seq(
          new Label("Login") { style = titleStyle },
          userField, passField, loginButton, registerButton, backButton, messageLabel
        )
      }
    }
  }

  // ================= REGISTER =================
  def registerScene(): Scene = {
    val fullNameField = styledField(new TextField { promptText = "Full Name" })
    val userField = styledField(new TextField { promptText = "Username" })
    val emailField = styledField(new TextField { promptText = "Email Address" })
    val phoneField = styledField(new TextField { promptText = "Phone Number" })
    val passField = styledPassField(new PasswordField { promptText = "Password" })
    val confirmField = styledPassField(new PasswordField { promptText = "Confirm Password" })
    val messageLabel = new Label("") { style = errorLabelStyle }

    val submitButton = primaryButton("Register") {
      AuthService.register(
        fullNameField.text.value, emailField.text.value, userField.text.value,
        passField.text.value, confirmField.text.value, phoneField.text.value
      ) match {
        case Right(newUser) => stage.scene = homeScene(newUser)
        case Left(err)      => messageLabel.text = err
      }
    }
    val backButton = secondaryButton("Already have an account? Login") { stage.scene = loginScene() }

    new Scene(WindowWidth, WindowHeight) {
      root = scrollable(new VBox(12) {
        padding = Insets(30); alignment = Pos.Center; style = bgStyle
        children = Seq(
          new Label("Create Your Account") { style = titleStyle },
          fullNameField, userField, emailField, phoneField, passField, confirmField,
          submitButton, backButton, messageLabel
        )
      })
    }
  }


  // ai-assisted: #9
  // why: Used AI to implement the FamilyCareFund homepage layout and navigation.
  // ================= HOMEPAGE =================
  def homeScene(user: HouseholdUser): Scene = {
    draftLoadedFor = None
    val household = FileStorage.loadHouseholds().find(_.householdId == user.householdId)

    val infoBox = new VBox(6) {
      style = cardStyle
      children = Seq(
        new Label("What is FamilyCareFund?") { style = "-fx-font-weight: bold;" },
        new Label("FamilyCareFund is a subsidy eligibility system designed to help low-income households identify government subsidies they may qualify for. Provide your household information and the system will check your eligibility and generate a personalised application checklist.") {
          style = subLabelStyle; wrapText = true
        }
      )
    }

    val checkEligibilityBtn = primaryButton("Check My Eligibility") {
      household match {
        case None    => stage.scene = householdFormScene(user)
        case Some(_) => stage.scene = reviewScene(user)
      }
    }

    def sectionCard(title: String, desc: String)(action: => Unit): VBox = {
      val box = new VBox(6) {
        style = cardStyle
        children = Seq(
          new Label(title) { style = "-fx-font-weight: bold;" },
          new Label(desc) { style = subLabelStyle; wrapText = true },
          secondaryButton("Open") { action }
        )
      }
      HBox.setHgrow(box, Priority.Always)
      box
    }

    val myHouseholdRow = new HBox(14) {
      children = Seq(
        sectionCard("Household Information",
          if (household.isEmpty) "Complete your household details to check your eligibility."
          else "Update your household details anytime.") { stage.scene = householdFormScene(user) },
        sectionCard("Subsidies", "Explore the 6 subsidies available through FamilyCareFund.") { stage.scene = exploreSubsidiesScene(user) }
      )
    }

    val myApplicationRow = new HBox(14) {
      children = Seq(
        sectionCard("Eligibility Results", "View subsidies your household qualifies for.") {
          if (household.isEmpty) info("Please complete your household information first.") else stage.scene = eligibilityResultsScene(user)
        },
        sectionCard("Need Help?", "Request support from NGO partners for guidance during the subsidy application process.") { stage.scene = ngoAssistanceScene(user) }
      )
    }

    val mainContent = scrollable(new VBox(16) {
      padding = Insets(30)
      children = Seq(
        new Label(s"Welcome back, ${user.fullName}!") { style = titleStyle },
        infoBox, checkEligibilityBtn,
        new Label("MY HOUSEHOLD") { style = "-fx-font-weight: bold; -fx-text-fill: #55707d;" }, myHouseholdRow,
        new Label("MY APPLICATION") { style = "-fx-font-weight: bold; -fx-text-fill: #55707d;" }, myApplicationRow
      )
    })
    VBox.setVgrow(mainContent, Priority.Always)

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "home"), mainContent)
      }
    }
  }

  // ai-assisted: #10
  // why: Used AI to implement the ScalaFX household information form and its input controls.
  // ================= HOUSEHOLD INFORMATION FORM =================
  def householdFormScene(user: HouseholdUser): Scene = {
    val existingHousehold = FileStorage.loadHouseholds().find(_.householdId == user.householdId)
    val existingExtras = FileStorage.findExtras(user.username)

    // Only pull from saved files the FIRST time we enter this household's
    // form - after that, everything reads/writes to `draft` and `draftMembers`
    // in memory, so navigating to Add Member and back keeps everything typed.
    if (draftLoadedFor != Some(user.householdId)) {
      draftMembers = ObservableBuffer.from(existingHousehold.map(_.members).getOrElse(List.empty))
      val self = draftMembers.find(_.name == user.fullName)
      draft = new HouseholdDraft()
      draft.age = self.map(_.age.toString).getOrElse("")
      draft.employment = self.map(_.employmentStatus).orNull
      draft.income = self.map(_.monthlyIncome.toString).getOrElse("")
      draft.dependent = self.exists(_.dependent)
      draft.householdCount = existingHousehold.map(_.members.length.toString).getOrElse("1")
      draft.housing = existingHousehold.map(_.housingStatus).getOrElse("Renting")
      draft.vehicle = existingExtras.map(_.vehicleOwnership).getOrElse("No")
      val existingPropertyCount = existingHousehold.map(_.additionalPropertyCount).getOrElse(0)
      draft.additionalProperty = if (existingPropertyCount > 0) "Yes" else "No"
      draft.propertyCount = existingPropertyCount.toString
      draft.singleParent = existingHousehold.exists(_.isSingleParent)
      draft.receivesAssistance = if (existingExtras.exists(_.receivesFinancialAssistance)) "Yes" else "No"
      draft.assistanceType = existingExtras.flatMap(_.assistanceType).orNull
      draft.savings = existingExtras.map(_.savingsRange).getOrElse("Prefer not to say")
      draft.disability = existingExtras.exists(_.hasDisability)
      draft.care = existingExtras.exists(_.needsCare)
      draftLoadedFor = Some(user.householdId)
    }

    // ----- Step 1: household size + your info -----
    val householdCountField = styledField(new TextField { promptText = "Number of people in your household"; text = draft.householdCount })

    val selfAgeField = styledField(new TextField { promptText = "Enter your age"; text = draft.age })

    val selfEmploymentBox = new ComboBox[String](
      ObservableBuffer("Employed", "Self-employed", "Unemployed", "Student", "Retired", "Unable to work", "Other")
    ) { style = fieldStyle }
    if (draft.employment != null) selfEmploymentBox.value = draft.employment

    val selfIncomeField = styledField(new TextField { promptText = "Enter your monthly income (RM)"; text = draft.income })

    val selfDependentCheck = new CheckBox("Are you a dependent?") { selected = draft.dependent }

    // ----- Other members list -----
    val membersListVBox = new VBox(8)

    // ----- Step 2: household information -----
    val housingBox = new ComboBox[String](
      ObservableBuffer("Own home", "Renting", "Living with family/relatives", "Public/government housing", "Other")
    ) { style = fieldStyle }
    housingBox.value = draft.housing

    val vehicleBox = new ComboBox[String](
      ObservableBuffer("No", "Motorcycle", "Car", "Motorcycle and car", "Other")
    ) { style = fieldStyle }
    vehicleBox.value = draft.vehicle

    val additionalPropertyBox = new ComboBox[String](ObservableBuffer("No", "Yes")) { style = fieldStyle }
    additionalPropertyBox.value = draft.additionalProperty

    val propertyCountField = styledField(new TextField { promptText = "How many additional properties?"; text = draft.propertyCount })
    propertyCountField.disable = draft.additionalProperty != "Yes"
    if (draft.additionalProperty != "Yes") propertyCountField.text = "0"

    additionalPropertyBox.value.onChange { (_, _, newVal) =>
      val isYes = newVal == "Yes"
      propertyCountField.disable = !isYes
      if (!isYes) propertyCountField.text = "0"
    }

    val singleParentCheck = new CheckBox("This is a single-parent household") { selected = draft.singleParent }

    // ----- Step 3: financial situation -----
    val receivesAssistanceBox = new ComboBox[String](ObservableBuffer("No", "Yes")) { style = fieldStyle }
    receivesAssistanceBox.value = draft.receivesAssistance

    val assistanceTypeBox = new ComboBox[String](
      ObservableBuffer("Government assistance", "Pension", "Allowance", "Other")
    ) { style = fieldStyle }
    if (draft.assistanceType != null) assistanceTypeBox.value = draft.assistanceType
    assistanceTypeBox.disable = draft.receivesAssistance != "Yes"

    receivesAssistanceBox.value.onChange { (_, _, newVal) =>
      assistanceTypeBox.disable = newVal != "Yes"
      if (newVal != "Yes") assistanceTypeBox.value = null
    }

    val savingsBox = new ComboBox[String](
      ObservableBuffer("Below RM1,000", "RM1,000-RM4,999", "RM5,000-RM9,999", "RM10,000-RM49,999", "RM50,000 or more", "Prefer not to say")
    ) { style = fieldStyle }
    savingsBox.value = draft.savings

    // ----- Step 4: special circumstances -----
    val disabilityCheck = new CheckBox("Does anyone in the household have a disability?") { selected = draft.disability }
    val careCheck = new CheckBox("Does anyone in the household require regular care or medical support?") { selected = draft.care }

    val messageLabel = new Label("") { style = errorLabelStyle; wrapText = true }

    // Captures every current field's value back into `draft` before we
    // navigate away to Add Member, so nothing gets lost.
    def syncDraftFromFields(): Unit = {
      draft.age = selfAgeField.text.value
      draft.employment = selfEmploymentBox.value.value
      draft.income = selfIncomeField.text.value
      draft.dependent = selfDependentCheck.selected.value
      draft.householdCount = householdCountField.text.value
      draft.housing = housingBox.value.value
      draft.vehicle = vehicleBox.value.value
      draft.additionalProperty = additionalPropertyBox.value.value
      draft.propertyCount = propertyCountField.text.value
      draft.singleParent = singleParentCheck.selected.value
      draft.receivesAssistance = receivesAssistanceBox.value.value
      draft.assistanceType = assistanceTypeBox.value.value
      draft.savings = savingsBox.value.value
      draft.disability = disabilityCheck.selected.value
      draft.care = careCheck.selected.value
    }

    def renderMembers(): Unit = {
      membersListVBox.children.clear()
      draftMembers.filterNot(_.name == user.fullName).foreach { m =>
        val detailsBox = new VBox(3) {
          children = Seq(
            new Label(s"${m.name} (${m.relationship})") {
              style = "-fx-font-weight: bold;"
            },
            new Label(
              s"Age ${m.age} | ${m.employmentStatus} | RM${m.monthlyIncome.toInt}/month | " +
                s"${if (m.dependent) "Dependent" else "Not a dependent"}"
            ) {
              style = subLabelStyle
            }
          )
        }
        HBox.setHgrow(detailsBox, Priority.Always)

        val editButton = new Button("Edit") {
          style = secondaryButtonStyle
          onAction = _ => {
            syncDraftFromFields()
            stage.scene = addMemberScene(user, () => stage.scene = householdFormScene(user), Some(m))
          }
        }
        val removeButton = new Button("Remove") {
          style = dangerButtonStyle
          onAction = _ => {
            draftMembers -= m; renderMembers()
          }
        }

        val row = new HBox(10) {
          style = cardStyle
          alignment = Pos.CenterLeft
          children = Seq(detailsBox, editButton, removeButton)
        }
        membersListVBox.children.add(row)
      }
    }
    renderMembers()

    val addMemberButton = secondaryButton("+ Add Household Member") {
      syncDraftFromFields()
      stage.scene = addMemberScene(user, () => stage.scene = householdFormScene(user))
    }

    // ----- Save & Review -----
    val saveButton = primaryButton("Save & Review") {
      syncDraftFromFields()

      val parsed = for {
        householdCount <- parseIntSafe(draft.householdCount)
        selfAge <- parseIntSafe(draft.age)
        selfIncome <- parseDoubleSafe(if (draft.income.trim.isEmpty) "0" else draft.income)
        propCount <- parseIntSafe(if (draft.additionalProperty == "Yes") { if (draft.propertyCount.trim.isEmpty) "0" else draft.propertyCount } else "0")
      } yield (householdCount, selfAge, selfIncome, propCount)

      parsed match {
        case Left(err) => messageLabel.text = err
        case Right((householdCount, selfAge, selfIncome, propCount)) =>
          val currentOtherMembers = draftMembers.filterNot(_.name == user.fullName).size

          if (householdCount < 1) {
            messageLabel.text = "Household size must be at least 1."
          } else if (householdCount != currentOtherMembers + 1) {
            messageLabel.text = s"Household size is $householdCount, but you currently have ${currentOtherMembers + 1} household member(s) entered."
          } else if (draft.employment == null) {
            messageLabel.text = "Please select your employment status."
          } else if (draft.receivesAssistance == "Yes" && draft.assistanceType == null) {
            messageLabel.text = "Please select the type of financial assistance received."
          } else {
            val self = HouseholdMember(user.fullName, selfAge, "Self", draft.employment, selfIncome, draft.dependent)
            val allMembers = self :: draftMembers.filterNot(_.name == user.fullName).toList

            val household = Household(
              user.householdId, draft.housing, allMembers,
              draft.singleParent, existingHousehold.exists(_.needsNgoAssistance), propCount
            )

            household.validate() match {
              case Left(err) => messageLabel.text = err
              case Right(_) =>
                allMembers.map(_.validate()).collectFirst { case Left(e) => e } match {
                  case Some(err) => messageLabel.text = err
                  case None =>
                    FileStorage.saveHousehold(household)
                    val extras = HouseholdExtras(
                      user.username, draft.vehicle, draft.receivesAssistance == "Yes",
                      if (draft.receivesAssistance == "Yes") Option(draft.assistanceType) else None,
                      draft.savings, draft.disability, draft.care
                    )
                    FileStorage.saveExtras(extras)
                    draftLoadedFor = None
                    stage.scene = reviewScene(user)
                }
            }
          }
      }
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(
          topNav(user, "household"),
          scrollable(new VBox(16) {
            padding = Insets(30)
            children = Seq(
              new Label("Household Information") { style = titleStyle },
              new Label("Please provide information about your household. This information will be used to check which subsidies you may be eligible for.") {
                style = subLabelStyle; wrapText = true
              },

              new Label("STEP 1 - HOUSEHOLD MEMBERS") { style = stepTitleStyle },
              new Label("How many people live in your household?") { style = "-fx-font-weight: bold;" },
              householdCountField,

              new Label("Your Information") { style = "-fx-font-weight: bold;" },
              new Label("What is your age?") { style = subLabelStyle }, selfAgeField,
              new Label("What is your employment status?") { style = subLabelStyle }, selfEmploymentBox,
              new Label("What is your monthly income?") { style = subLabelStyle }, selfIncomeField,
              selfDependentCheck,

              new Label("Other Household Members") { style = "-fx-font-weight: bold;" },
              new Label("Add other people who live in your household.") { style = subLabelStyle; wrapText = true },
              membersListVBox, addMemberButton,

              new Label("STEP 2 - HOUSEHOLD INFORMATION") { style = stepTitleStyle },
              new Label("What is your household's housing situation?") { style = subLabelStyle }, housingBox,
              new Label("Does your household own any vehicles?") { style = subLabelStyle }, vehicleBox,
              new Label("Does your household own any additional property besides your current home?") { style = subLabelStyle; wrapText = true }, additionalPropertyBox,
              new Label("If yes, how many additional properties?") { style = subLabelStyle }, propertyCountField,
              singleParentCheck,

              new Label("STEP 3 - FINANCIAL SITUATION") { style = stepTitleStyle },
              new Label("Does your household currently receive any financial assistance?") { style = subLabelStyle; wrapText = true }, receivesAssistanceBox,
              new Label("If yes, what type of assistance does your household receive?") { style = subLabelStyle }, assistanceTypeBox,
              new Label("Approximately how much savings does your household currently have?") { style = subLabelStyle; wrapText = true }, savingsBox,

              new Label("STEP 4 - SPECIAL CIRCUMSTANCES") { style = stepTitleStyle },
              disabilityCheck, careCheck,

              saveButton, messageLabel
            )
          })
        )
      }
    }
  }

  def addMemberScene(user: HouseholdUser, onDone: () => Unit, editing: Option[HouseholdMember] = None): Scene = {
    val nameField = styledField(new TextField { promptText = "Name"; text = editing.map(_.name).getOrElse("") })
    val relationshipBox = new ComboBox[String](ObservableBuffer("Parent", "Spouse", "Child", "Sibling", "Grandparent", "Other")) { style = fieldStyle }
    editing.foreach(m => relationshipBox.value = m.relationship)
    val ageField = styledField(new TextField { promptText = "Age"; text = editing.map(_.age.toString).getOrElse("") })
    val employmentBox = new ComboBox[String](ObservableBuffer("Employed", "Self-employed", "Unemployed", "Student", "Retired", "Unable to work", "Other")) { style = fieldStyle }
    editing.foreach(m => employmentBox.value = m.employmentStatus)
    val incomeField = styledField(new TextField { promptText = "Monthly income (RM), 0 if none"; text = editing.map(_.monthlyIncome.toString).getOrElse("") })
    val dependentCheck = new CheckBox("Is this household member a dependent?") { selected = editing.exists(_.dependent) }
    val messageLabel = new Label("") { style = errorLabelStyle }

    val saveButton = primaryButton(if (editing.isDefined) "Update Member" else "Save Member") {
      val parsed = for {
        age <- parseIntSafe(ageField.text.value)
        income <- parseDoubleSafe(if (incomeField.text.value.trim.isEmpty) "0" else incomeField.text.value)
      } yield (age, income)

      parsed match {
        case Left(err) => messageLabel.text = err
        case Right((age, income)) =>
          if (nameField.text.value.trim.isEmpty) messageLabel.text = "Name cannot be empty"
          else if (relationshipBox.value.value == null) messageLabel.text = "Please select a relationship"
          else if (employmentBox.value.value == null) messageLabel.text = "Please select an employment status"
          else {
            val member = HouseholdMember(nameField.text.value, age, relationshipBox.value.value, employmentBox.value.value, income, dependentCheck.selected.value)
            member.validate() match {
              case Left(err) => messageLabel.text = err
              case Right(_) =>
                editing match {
                  case Some(old) =>
                    val idx = draftMembers.indexOf(old)
                    if (idx >= 0) draftMembers.update(idx, member) else draftMembers += member
                  case None => draftMembers += member
                }
                onDone()
            }
          }
      }
    }
    val cancelButton = secondaryButton("Cancel") { onDone() }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "household"), new VBox(12) {
          padding = Insets(30)
          children = Seq(
            new Label(if (editing.isDefined) "Edit Household Member" else "Add Household Member") { style = titleStyle },
            new Label("Name") { style = subLabelStyle }, nameField,
            new Label("Relationship to you") { style = subLabelStyle }, relationshipBox,
            new Label("Age") { style = subLabelStyle }, ageField,
            new Label("Employment Status") { style = subLabelStyle }, employmentBox,
            new Label("Monthly income") { style = subLabelStyle }, incomeField, dependentCheck,
            saveButton, cancelButton, messageLabel
          )
        })
      }
    }
  }

  // ================= REVIEW =================
  def reviewScene(user: HouseholdUser): Scene = {
    FileStorage.loadHouseholds().find(_.householdId == user.householdId) match {
      case None => householdFormScene(user)
      case Some(h) =>
        val extras = FileStorage.findExtras(user.username)
        val membersText = h.members.map(m =>
          s"${m.name} - Age ${m.age}, ${m.relationship}, ${m.employmentStatus}, RM${m.monthlyIncome.toInt}${if (m.dependent) " (dependent)" else ""}"
        ).mkString("\n")

        val extrasText = extras.map(e =>
          s"Vehicle: ${e.vehicleOwnership}\n" +
            s"Financial assistance: ${if (e.receivesFinancialAssistance) e.assistanceType.getOrElse("Yes") else "No"}\n" +
            s"Savings: ${e.savingsRange}\n" +
            s"Disability in household: ${if (e.hasDisability) "Yes" else "No"}\n" +
            s"Needs care: ${if (e.needsCare) "Yes" else "No"}"
        ).getOrElse("")

        val summary = new Label(
          s"Household Members\n$membersText\n\n" +
            s"Housing: ${h.housingStatus}\n" +
            s"Additional properties: ${h.additionalPropertyCount}\n" +
            s"Single-parent household: ${if (h.isSingleParent) "Yes" else "No"}\n\n" +
            extrasText
        ) { style = "-fx-font-size: 13px;"; wrapText = true }

        new Scene(WindowWidth, WindowHeight) {
          root = new VBox {
            style = bgStyle
            children = Seq(topNav(user, "home"), scrollable(new VBox(14) {
              padding = Insets(30)
              children = Seq(
                new Label("Review Your Information") { style = titleStyle },
                summary,
                new HBox(10) {
                  children = Seq(
                    secondaryButton("Edit Information") { stage.scene = householdFormScene(user) },
                    primaryButton("Confirm & Check Eligibility") { stage.scene = eligibilityResultsScene(user) }
                  )
                }
              )
            }))
          }
        }
    }
  }

  // ================= ELIGIBILITY RESULTS =================
  def eligibilityResultsScene(user: HouseholdUser): Scene = {
    FileStorage.loadHouseholds().find(_.householdId == user.householdId) match {
      case None =>
        new Scene(WindowWidth, WindowHeight) {
          root = new VBox {
            style = bgStyle
            children = Seq(topNav(user, "home"), new VBox(12) {
              padding = Insets(30)
              children = Seq(
                new Label("Please complete your household information first.") { style = titleStyle },
                primaryButton("Enter Household Details") { stage.scene = householdFormScene(user) }
              )
            })
          }
        }
      case Some(household) =>
        val results = EligibilityChecker.checkAll(household, Subsidy.all)
        val eligible = results.filter(_.isEligible())
        val notEligible = results.filterNot(_.isEligible())

        def resultRow(r: EligibilityResult[Subsidy]): HBox = {
          val nameBox = new VBox(2)
          HBox.setHgrow(nameBox, Priority.Always)
          nameBox.children = Seq(new Label(r.item.name) { style = "-fx-font-weight: bold;" })
          val row = new HBox(10) {
            style = cardStyle
            alignment = Pos.CenterLeft
            children = Seq(
              nameBox,
              new Label(if (r.isEligible()) "ELIGIBLE" else "NOT ELIGIBLE") { style = if (r.isEligible()) eligibleStyle else notEligibleStyle }
            )
          }
          if (r.isEligible()) row.onMouseClicked = _ => stage.scene = subsidyDetailScene(user, r.item, Some(household), true, () => stage.scene = eligibilityResultsScene(user))
          row
        }

        val eligibleBox = new VBox(8); eligible.foreach(r => eligibleBox.children.add(resultRow(r)))
        val notEligibleBox = new VBox(8); notEligible.foreach(r => notEligibleBox.children.add(resultRow(r)))

        new Scene(WindowWidth, WindowHeight) {
          root = new VBox {
            style = bgStyle
            children = Seq(topNav(user, "home"), scrollable(new VBox(14) {
              padding = Insets(30)
              children = Seq(
                new Label("Your Eligibility Results") { style = titleStyle },
                new Label(s"Based on the information provided, you may be eligible for ${eligible.length} subsidies.") { style = subLabelStyle },
                new Label("Eligible Subsidies (click to view details)") { style = "-fx-font-weight: bold;" }, eligibleBox,
                new Label("Not Eligible") { style = "-fx-font-weight: bold;" }, notEligibleBox
              )
            }))
          }
        }
    }
  }

  // ai-assisted: #13
  // why: Used AI to implement subsidy browsing, subsidy details and application checklist screens.
  // ================= SUBSIDY DETAIL =================
  def subsidyDetailScene(user: HouseholdUser, s: Subsidy, household: Option[Household], eligibleMode: Boolean, onBack: () => Unit): Scene = {
    val whyBox = new VBox(4)
    if (eligibleMode) {
      whyBox.children.add(new Label("Why you qualify:") { style = "-fx-font-weight: bold;" })
      whyBox.children.add(new Label(s.eligibilityCriteria) { style = eligibleStyle; wrapText = true })
    }

    val docsList = s.requiredDocuments.map(d => s"- $d").mkString("\n")

    val actionsRow = new HBox(10) {
      children =
        if (eligibleMode) Seq(
          primaryButton("Generate Checklist") { stage.scene = checklistScene(user, s) },
          secondaryButton("Need NGO Assistance") { stage.scene = ngoAssistanceScene(user) },
          secondaryButton("Back") { onBack() }
        ) else Seq(
          secondaryButton("Need NGO Assistance") { stage.scene = ngoAssistanceScene(user) },
          secondaryButton("Back") { onBack() }
        )
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "subsidies"), scrollable(new VBox(12) {
          padding = Insets(30)
          children = Seq(
            new Label(s.name) { style = titleStyle },
            new Label(s.description) { style = subLabelStyle; wrapText = true },
            new Label(s"Benefits: ${s.benefits}") { style = "-fx-font-size: 13px;" },
            new Label(s"Eligibility criteria: ${s.eligibilityCriteria}") { style = "-fx-font-size: 13px;"; wrapText = true },
            whyBox,
            new Label("Required Documents:") { style = "-fx-font-weight: bold; -fx-padding: 10 0 0 0;" },
            new Label(docsList) { style = "-fx-font-size: 13px;" },
            new Label("How to Apply: Prepare the required documents and submit your application through the relevant official channel, or request assistance below.") {
              style = subLabelStyle; wrapText = true
            },
            actionsRow
          )
        }))
      }
    }
  }

  // ================= CHECKLIST =================
  def checklistScene(user: HouseholdUser, s: Subsidy): Scene = {
    val checklist = ApplicationChecklist.generateChecklist(s)
    val docsBox = new VBox(6)
    checklist.documents.foreach(d => docsBox.children.add(new Label(s"[ ] $d") { style = "-fx-font-size: 13px;" }))

    val printButton = primaryButton("Print Checklist") {
      checklist.printChecklist()
      info("Checklist printed to console/output.")
    }
    val backButton = secondaryButton("Back") {
      stage.scene = subsidyDetailScene(user, s, FileStorage.loadHouseholds().find(_.householdId == user.householdId), true, () => stage.scene = eligibilityResultsScene(user))
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "subsidies"), new VBox(14) {
          padding = Insets(30)
          children = Seq(new Label(s"${s.name} Checklist") { style = titleStyle }, docsBox, printButton, backButton)
        })
      }
    }
  }

  // ================= NGO ASSISTANCE =================
  def ngoAssistanceScene(user: HouseholdUser): Scene = {
    val submitButton = primaryButton("Submit Assistance Request") {
      FileStorage.loadHouseholds().find(_.householdId == user.householdId) match {
        case Some(h) => FileStorage.saveHousehold(h.copy(needsNgoAssistance = true))
        case None    => ()
      }
      info("Request Submitted Successfully!\nYour request has been recorded. An NGO representative will contact you for further assistance.")
      stage.scene = homeScene(user)
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "home"), new VBox(12) {
          padding = Insets(30)
          children = Seq(
            new Label("NGO Assistance") { style = titleStyle },
            new Label("Need help with your application? Our NGO partners can assist with understanding subsidy requirements, preparing documents, and completing application forms.") {
              style = subLabelStyle; wrapText = true
            },
            new Label("Contact: FamilyCare NGO Support Centre") { style = "-fx-font-size: 13px;" },
            new Label("Phone: 03-1234 8892") { style = "-fx-font-size: 13px;" },
            new Label("Email: support@familycare.org") { style = "-fx-font-size: 13px;" },
            submitButton
          )
        })
      }
    }
  }

  // ================= EXPLORE SUBSIDIES (all 6, browsing) =================
  def exploreSubsidiesScene(user: HouseholdUser): Scene = {
    val household = FileStorage.loadHouseholds().find(_.householdId == user.householdId)
    val listBox = new VBox(8)
    Subsidy.all.foreach { s =>
      val row = new VBox(4) {
        style = cardStyle
        children = Seq(new Label(s.name) { style = "-fx-font-weight: bold;" }, new Label(s.description) { style = subLabelStyle; wrapText = true })
      }
      row.onMouseClicked = _ => stage.scene = subsidyDetailScene(user, s, household, false, () => stage.scene = exploreSubsidiesScene(user))
      listBox.children.add(row)
    }
    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(topNav(user, "subsidies"), scrollable(new VBox(14) {
          padding = Insets(30)
          children = Seq(new Label("Explore the 6 Subsidies") { style = titleStyle }, listBox)
        }))
      }
    }
  }

  // ================= FAQ =================
  def faqScene(user: Option[HouseholdUser]): Scene = {
    val faqBox = new VBox(12)
    val faqs = FileStorage.loadFAQs()

    if (faqs.isEmpty) {
      faqBox.children.add(new Label("No FAQs available at the moment.") { style = subLabelStyle })
    } else {
      faqs.foreach { f =>
        faqBox.children.add(new VBox(6) {
          style = cardStyle
          children = Seq(
            new Label(s"Q: ${f.question}") { style = "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #205072;" },
            new Label(s"A: ${f.answer}") { style = subLabelStyle; wrapText = true }
          )
        })
      }
    }

    val headerNav: HBox = user match {
      case Some(u) => topNav(u, "faq")
      case None =>
        val spacer = new Region(); HBox.setHgrow(spacer, Priority.Always)
        new HBox {
          style = navBarStyle
          alignment = Pos.CenterLeft
          children = Seq(
            new Label("FamilyCareFund") { style = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #205072; -fx-padding: 14 18 14 18;" },
            spacer,
            new Label("Back to Main") { style = navItemStyle; onMouseClicked = _ => stage.scene = landingScene() }
          )
        }
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(headerNav, scrollable(new VBox(16) {
          padding = Insets(30)
          children = Seq(
            new Label("Frequently Asked Questions") { style = titleStyle },
            new Label("Find quick answers about household registration and subsidy eligibility.") { style = subLabelStyle },
            faqBox
          )
        }))
      }
    }
  }

  // ================= ADMIN =================
  def adminTopBar(): HBox = {
    val spacer = new Region(); HBox.setHgrow(spacer, Priority.Always)
    new HBox {
      style = navBarStyle
      children = Seq(
        new Label("FamilyCareFund - Admin") { style = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #205072; -fx-padding: 14 18 14 18;" },
        spacer,
        new Label("Log out") { style = navItemStyle; onMouseClicked = _ => stage.scene = landingScene() }
      )
    }
  }

  def adminDashboard(): Scene = {
    val viewUsersBtn = new Button("View Users") { style = primaryButtonStyle + " -fx-min-width: 300px;"; onAction = _ => stage.scene = adminUsersScene() }
    val editFaqBtn = new Button("Edit FAQs") { style = primaryButtonStyle + " -fx-min-width: 300px;"; onAction = _ => stage.scene = adminFaqScene() }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(adminTopBar(), new VBox(14) {
          padding = Insets(30); alignment = Pos.Center
          children = Seq(
            new Label("Admin Dashboard") { style = titleStyle },
            new Label("Welcome to the Admin Portal") { style = subLabelStyle },
            new VBox(10) { alignment = Pos.Center; children = Seq(viewUsersBtn, editFaqBtn) }
          )
        })
      }
    }
  }

  def adminUsersScene(): Scene = {
    val users = FileStorage.loadUsers()
    val households = FileStorage.loadHouseholds()
    val listBox = new VBox(8)
    users.foreach { u =>
      val h = households.find(_.householdId == u.householdId)
      listBox.children.add(new VBox(2) {
        style = cardStyle
        children = Seq(
          new Label(s"${u.fullName} (${u.username})") { style = "-fx-font-weight: bold;" },
          new Label(s"${u.email} | ${u.phoneNumber}") { style = subLabelStyle },
          new Label(h.map(hh => s"Household: ${hh.members.length} members, RM${hh.calculateTotalIncome().toInt}/month total income\nNGO Assistance : ${if (hh.needsNgoAssistance) "Requested" else "Not requested"}").getOrElse("No household info yet")) { style = subLabelStyle }
        )
      })
    }
    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(adminTopBar(), scrollable(new VBox(14) {
          padding = Insets(30)
          children = Seq(new Label("Registered Households") { style = titleStyle }, listBox, secondaryButton("Back") { stage.scene = adminDashboard() })
        }))
      }
    }
  }

  def adminFaqScene(): Scene = {
    val listBox = new VBox(10)

    def render(): Unit = {
      listBox.children.clear()
      FileStorage.loadFAQs().foreach { f =>
        listBox.children.add(new VBox(4) {
          style = cardStyle
          children = Seq(
            new Label(f.question) { style = "-fx-font-weight: bold;" },
            new Label(f.answer) { style = subLabelStyle; wrapText = true },
            new Button("Delete") { style = dangerButtonStyle; onAction = _ => { FileStorage.deleteFAQ(f.question); render() } }
          )
        })
      }
    }
    render()

    val qField = styledField(new TextField { promptText = "Question" })
    val aField = styledField(new TextField { promptText = "Answer" })
    val messageLabel = new Label("") { style = errorLabelStyle }
    val addBtn = primaryButton("Add FAQ") {
      if (qField.text.value.trim.isEmpty || aField.text.value.trim.isEmpty) {
        messageLabel.text = "Both question and answer are required"
      } else {
        FileStorage.saveFAQ(FAQ(qField.text.value, aField.text.value))
        qField.text = ""; aField.text = ""; messageLabel.text = ""
        render()
      }
    }

    new Scene(WindowWidth, WindowHeight) {
      root = new VBox {
        style = bgStyle
        children = Seq(adminTopBar(), scrollable(new VBox(14) {
          padding = Insets(30)
          children = Seq(
            new Label("Edit FAQs") { style = titleStyle },
            listBox,
            new Label("Add / Edit FAQ") { style = "-fx-font-weight: bold;" },
            qField, aField, addBtn, messageLabel,
            secondaryButton("Back") { stage.scene = adminDashboard() }
          )
        }))
      }
    }
  }
}
