package com.example.data

data class CurriculumLesson(
    val id: String,
    val level: String, // "A1", "A2", "B1"
    val number: Int,
    val title: String,
    val description: String,
    val objectives: List<String>,
    val vocabulary: List<Triple<String, String, String>>, // Word, Article, Translation
    val grammarNotes: String,
    val importantSentences: List<Pair<String, String>>,
    val dialogues: List<Pair<String, String>>,
    val importantVerbs: List<String>,
    val speakingTask: String,
    val writingTask: String,
    val exercises: List<Triple<String, String, List<String>>> // Question, Answer, Options
)

data class RoadmapDay(
    val dayIndex: Int,
    val dateString: String,
    val title: String,
    val level: String, // "A1", "A2", "B1", "Review"
    val lessonIds: List<String>,
    val objectives: String,
    val vocabularyGoals: String,
    val grammarGoals: String,
    val listeningTasks: String,
    val speakingTasks: String,
    val writingTasks: String,
    val revisionTasks: String,
    val estimatedStudyTimeMinutes: Int,
    val isRevisionDay: Boolean = false,
    val isAssessmentDay: Boolean = false,
    val isMilestoneTest: Boolean = false
)

object Curriculum {
    val lessons = listOf(
        // --- A1 ---
        CurriculumLesson(
            id = "A1_L1",
            level = "A1",
            number = 1,
            title = "Hallo und Willkommen",
            description = "Learn how to greet people, introduce yourself, and state where you are from.",
            objectives = listOf("Greet people in German", "Introduce oneself (Name, Origin)", "Count from 1 to 10"),
            vocabulary = listOf(
                Triple("Hallo", "-", "Hello"),
                Triple("Tschüss", "-", "Goodbye"),
                Triple("Name", "der", "Name"),
                Triple("Herkunft", "die", "Origin"),
                Triple("Deutschland", "-", "Germany"),
                Triple("Telugu", "-", "Telugu language")
            ),
            grammarNotes = "Verb conjugation of 'sein' (to be) and 'kommen' (to come) in 1st & 2nd person singular:\n- Ich bin / Ich komme (I am / I come)\n- Du bist / Du kommst (You are / You come)",
            importantSentences = listOf(
                "Hallo! Ich heiße Uday." to "Hello! My name is Uday.",
                "Woher kommst du?" to "Where do you come from?",
                "Ich komme aus Indien." to "I come from India."
            ),
            dialogues = listOf(
                "Nico" to "Hallo, ich bin Nico. Und du?",
                "Uday" to "Hallo Nico! Ich bin Uday. Freut mich!",
                "Nico" to "Freut mich auch, Uday. Woher kommst du?",
                "Uday" to "Ich komme aus Indien, aus Hyderabad."
            ),
            importantVerbs = listOf("sein", "kommen", "heißen"),
            speakingTask = "Record yourself introducing yourself: your name, where you come from, and welcoming Nico to Germany.",
            writingTask = "Write a short paragraph introducing yourself (name, home country, native language Telugu, and fluent in English).",
            exercises = listOf(
                Triple("Ich ___ Uday. (sein)", "bin", listOf("bin", "bist", "ist")),
                Triple("Woher ___ du? (kommen)", "kommst", listOf("komme", "kommst", "kommt")),
                Triple("Wie ___ du? (heißen)", "heißt", listOf("heiße", "heißt", "heißen"))
            )
        ),
        CurriculumLesson(
            id = "A1_L2",
            level = "A1",
            number = 2,
            title = "Mein Zimmer (Wohnen)",
            description = "Describe your apartment, room, furniture, and colors.",
            objectives = listOf("Identify primary household items", "Use indefinite and definite articles", "Describe objects using colors"),
            vocabulary = listOf(
                Triple("Tisch", "der", "Table"),
                Triple("Stuhl", "der", "Chair"),
                Triple("Bett", "das", "Bed"),
                Triple("Lampe", "die", "Lamp"),
                Triple("Zimmer", "das", "Room"),
                Triple("groß", "-", "large/big")
            ),
            grammarNotes = "Definite (der, die, das) and Indefinite (ein, eine, ein) Articles in Nominative case.\n- Masculine: der Tisch / ein Tisch\n- Feminine: die Lampe / eine Lampe\n- Neuter: das Bett / ein Bett",
            importantSentences = listOf(
                "Der Tisch ist groß." to "The table is large.",
                "Ist das ein Bett?" to "Is that a bed?",
                "Nein, das ist kein Bett. Das ist ein Sofa." to "No, that is not a bed. That is a sofa."
            ),
            dialogues = listOf(
                "Uday" to "Nico, schau mal. Das ist mein Zimmer. Es ist klein, aber gemütlich.",
                "Nico" to "Sehr schön! Der Stuhl ist modern, und die Lampe ist sehr hell.",
                "Uday" to "Ja, und der Tisch ist perfekt für mein Engineering-Studium!"
            ),
            importantVerbs = listOf("wohnen", "haben", "finden"),
            speakingTask = "Describe the furniture in your study room. Make sure to specify the gender (der/die/das) for at least three items.",
            writingTask = "Describe your ideal room in Germany. List at least 5 pieces of furniture with their colors and dimensions (groß/klein).",
            exercises = listOf(
                Triple("Das ist ___ (ein/eine) Lampe.", "eine", listOf("ein", "eine", "einen")),
                Triple("___ (Der/Die/Das) Bett ist bequem.", "Das", listOf("Der", "Die", "Das")),
                Triple("Ich habe ___ (ein/eine) Stuhl.", "einen", listOf("ein", "eine", "einen"))
            )
        ),
        CurriculumLesson(
            id = "A1_L3",
            level = "A1",
            number = 3,
            title = "Meine Familie",
            description = "Learn how to talk about your family, siblings, parents, and marital status.",
            objectives = listOf("Introduce family members", "Use possessive pronouns (mein/dein)", "State professions simply"),
            vocabulary = listOf(
                Triple("Vater", "der", "Father"),
                Triple("Mutter", "die", "Mother"),
                Triple("Bruder", "der", "Brother"),
                Triple("Schwester", "die", "Sister"),
                Triple("Eltern", "die", "Parents"),
                Triple("ledig", "-", "single")
            ),
            grammarNotes = "Possessive Pronouns in Nominative:\n- Masculine/Neuter: mein (my), dein (your)\n- Feminine/Plural: meine (my), deine (your)",
            importantSentences = listOf(
                "Das ist meine Mutter." to "That is my mother.",
                "Hast du Geschwister?" to "Do you have siblings?",
                "Ja, ich habe einen Bruder." to "Yes, I have a brother."
            ),
            dialogues = listOf(
                "Nico" to "Wer ist das auf dem Foto, Uday?",
                "Uday" to "Das ist meine Familie in Indien. Das ist mein Vater, meine Mutter und das ist mein Bruder.",
                "Nico" to "Sieht sehr nett aus! Was machen sie beruflich?",
                "Uday" to "Mein Vater ist auch Ingenieur."
            ),
            importantVerbs = listOf("arbeiten", "leben", "lieben"),
            speakingTask = "Talk about your family members. State their names, relation to you, and what they do beruflich.",
            writingTask = "Write an introductory email introducing your family to a future flatmate in Germany.",
            exercises = listOf(
                Triple("Das ist ___ (mein/meine) Schwester.", "meine", listOf("mein", "meine", "meinen")),
                Triple("Wie heißt ___ (dein/deine) Vater?", "dein", listOf("dein", "deine", "deinen")),
                Triple("Meine ___ (Eltern/Bruder) leben in Indien.", "Eltern", listOf("Eltern", "Bruder", "Mutter"))
            )
        ),
        CurriculumLesson(
            id = "A1_L4",
            level = "A1",
            number = 4,
            title = "Essen und Trinken",
            description = "Navigate grocery shopping, markets, food vocabulary, and prices.",
            objectives = listOf("Order food and drinks in German", "Understand accusative articles for food items", "Enquire about prices"),
            vocabulary = listOf(
                Triple("Käse", "der", "Cheese"),
                Triple("Brot", "das", "Bread"),
                Triple("Apfel", "der", "Apple"),
                Triple("Milch", "die", "Milk"),
                Triple("Wasser", "das", "Water"),
                Triple("teuer", "-", "expensive")
            ),
            grammarNotes = "Accusative Case (Akkusativ) - Direct Object:\n- Only Masculine changes: der -> den, ein -> einen, kein -> keinen.\n- Feminine remains: die/eine, Neuter remains: das/ein.",
            importantSentences = listOf(
                "Ich möchte einen Apfel kaufen." to "I would like to buy an apple.",
                "Wie viel kostet das Brot?" to "How much does the bread cost?",
                "Das macht zusammen fünf Euro." to "That makes five Euros altogether."
            ),
            dialogues = listOf(
                "Verkäufer" to "Guten Tag! Was möchten Sie bitte?",
                "Uday" to "Guten Tag! Ich brauche ein Brot, einen Apfel und eine Flasche Milch.",
                "Verkäufer" to "Gerne. Sonst noch etwas?",
                "Uday" to "Nein danke. Wie viel kostet das?"
            ),
            importantVerbs = listOf("kaufen", "trinken", "essen", "möchten"),
            speakingTask = "Roleplay: You are at a German bakery. Order two breads and a coffee, and ask for the total price.",
            writingTask = "Create a grocery shopping list in German with articles, and write down two sentence formulas on asking for items.",
            exercises = listOf(
                Triple("Ich trinke ___ (einen/eine/ein) Tee.", "einen", listOf("einen", "eine", "ein")),
                Triple("Er isst ___ (ein/einen) Apfel.", "einen", listOf("ein", "einen", "eine")),
                Triple("Wir haben ___ (kein/keine) Brot mehr.", "kein", listOf("kein", "keine", "keinen"))
            )
        ),
        // --- A2 ---
        CurriculumLesson(
            id = "A2_L1",
            level = "A2",
            number = 1,
            title = "Wohnungssuche in Deutschland",
            description = "Navigate looking for flatshares (WG) and student accommodation, understanding leases and cold/warm rent.",
            objectives = listOf("Enquire about flat offerings", "Differentiate between Kaltmiete and Warmmiete", "Express spatial locations using dative prepositions"),
            vocabulary = listOf(
                Triple("Wohnung", "die", "Apartment"),
                Triple("WG-Zimmer", "das", "Flatshare room"),
                Triple("Miete", "die", "Rent"),
                Triple("Kaltmiete", "die", "Cold rent"),
                Triple("Nebenkosten", "die", "Utility costs (plural)"),
                Triple("Kaution", "die", "Security deposit")
            ),
            grammarNotes = "Two-Way Prepositions (Wechselpräpositionen) with Dative for locations (Wo?):\n- in, an, auf, unter, über, vor, hinter, neben, zwischen.\n- Examples: in dem (im) Zimmer, auf dem Tisch, in der WG.",
            importantSentences = listOf(
                "Ist das Zimmer noch frei?" to "Is the room still free?",
                "Wie hoch ist die Kaution?" to "How much is the deposit?",
                "Die Miete ist warm und inklusive Nebenkosten." to "The rent is warm and includes utilities."
            ),
            dialogues = listOf(
                "Vermieter" to "Hallo Uday! Kommen Sie rein. Das ist das Zimmer für Sie.",
                "Uday" to "Vielen Dank! Es liegt sehr ruhig. Ist das Bad auf dem Flur?",
                "Vermieter" to "Ja, Sie teilen es mit zwei anderen Studenten. Die Kaltmiete beträgt 350 Euro."
            ),
            importantVerbs = listOf("suchen", "mieten", "besichtigen", "umziehen"),
            speakingTask = "Call a landlord in Germany: Ask if the room is still available, enquire about the Warmmiete, and ask when you can visit it.",
            writingTask = "Write a WG-Bewerbung (WG application profile) explaining why you are the perfect flatmate physically moving from India as an engineering master's student.",
            exercises = listOf(
                Triple("Das Buch liegt auf ___ (dem/den) Tisch.", "dem", listOf("dem", "den", "der")),
                Triple("Ich wohne in ___ (einer/eine) WG.", "einer", listOf("einer", "eine", "einem")),
                Triple("Die Nebenkosten sind in ___ (der/die) Kaution nicht enthalten.", "der", listOf("der", "die", "dem"))
            )
        ),
        // --- B1 ---
        CurriculumLesson(
            id = "B1_L1",
            level = "B1",
            number = 1,
            title = "Das deutsche Universitätssystem",
            description = "Introduction to studies, lectures, master's requirements, and student services (Studierendenwerk).",
            objectives = listOf("Understand typical university vocabulary", "Formulate formal academic questions", "Talk about fields of engineering"),
            vocabulary = listOf(
                Triple("Universität", "die", "University"),
                Triple("Vorlesung", "die", "Lecture"),
                Triple("Studiengang", "der", "Course of study"),
                Triple("Prüfung", "die", "Examination"),
                Triple("Einschreibung", "die", "Matriculation"),
                Triple("Fachbereich", "der", "Department")
            ),
            grammarNotes = "Relative Clauses in Nominative, Accusative, and Dative cases:\n- Der Student, der aus Indien kommt, lernt Deutsch.\n- Die Vorlesung, die ich besuche, ist sehr interessant.",
            importantSentences = listOf(
                "Wann beginnt die Einschreibung für das Wintersemester?" to "When does enrollment for the winter semester begin?",
                "Ich studiere Maschinenbau im Masterstudiengang." to "I am studying mechanical engineering in the master's course.",
                "Wo finde ich das Sekretariat des Fachbereichs?" to "Where can I find the department's office?"
            ),
            dialogues = listOf(
                "Sekretärin" to "Guten Tag, Herr Uday. Wie kann ich Ihnen helfen?",
                "Uday" to "Guten Tag. Ich möchte meine Dokumente für die Matrikulation abgeben. Das ist mein Zulassungsbescheid.",
                "Sekretärin" to "Sehr schön. Hier ist Ihr Studentenausweis. Die Vorlesungen beginnen am 15. Oktober."
            ),
            importantVerbs = listOf("studieren", "bestehen", "anmelden", "immatrikulieren"),
            speakingTask = "Simulate an inquiry at the university's International Office about health insurance requirements and course schedules.",
            writingTask = "Write an essay (approx. 120 words) explaining why you chose to pursue your Master's degree in Engineering in Germany and your expectations of university life.",
            exercises = listOf(
                Triple("Der Zoom-Link, ___ (den/der) ich brauche, ist im Portal.", "den", listOf("den", "der", "dem")),
                Triple("Die Studentin, ___ (die/der) neben mir sitzt, kommt aus Deutschland.", "die", listOf("die", "der", "den")),
                Triple("Das Studienfach, für ___ (das/dem) ich mich interessiere, ist Informatik.", "das", listOf("das", "dem", "den"))
            )
        )
    )

    // Build standard 60-day roadmap beginning 1 July 2026 and ending 29 August 2026.
    val roadmap = (1..60).map { day ->
        val dateString = when {
            day <= 31 -> "$day July 2026"
            else -> "${day - 31} August 2026"
        }

        val level = when {
            day <= 15 -> "A1"
            day <= 35 -> "A2"
            day <= 55 -> "B1"
            else -> "EXAM"
        }

        val isRevision = day % 7 == 0 || day == 57 || day == 58
        val isAssessment = day % 7 == 0 && day < 56
        val isMilestone = day == 15 || day == 35 || day == 55 || day == 59
        val isFinalExam = day == 60

        val (title, lessonIds, minutes) = when {
            isFinalExam -> Triple("Goethe B1 Final Examination Simulator", emptyList(), 180)
            isMilestone -> {
                val milLevel = if (day <= 15) "A1" else if (day <= 35) "A2" else "B1"
                Triple("$milLevel Milestone Mock Examination", emptyList(), 120)
            }
            isRevision -> Triple("Weekly Reflection, Spaced Repetition & Assessment", emptyList(), 90)
            else -> {
                // Return dynamic lesson mappings
                val levelLessonNo = when (level) {
                    "A1" -> ((day - (day / 7)) % 4) + 1  // Cyclic helper
                    "A2" -> 1
                    else -> 1
                }
                val id = if (level == "A1") "A1_L$levelLessonNo" else if (level == "A2") "A2_L1" else "B1_L1"
                val les = lessons.firstOrNull { it.id == id }
                Triple("Lesson ${les?.number ?: 1}: ${les?.title ?: "German Grammar & Conversation"}", listOf(id), 120)
            }
        }

        val objectives = when {
            isFinalExam -> "Complete writing, reading, listening, and speaking portions of a certified Goethe B1 style exam."
            isMilestone -> "Verify your grasp of grammar, vocabulary, and active communication protocols for $level."
            isRevision -> "Reinforce vocabulary and review tough grammar structures stored in the Notebook."
            else -> "Master Nicos Weg level concepts, conjugate major verbs, and apply them in daily journal practice."
        }

        val vocabGoals = if (isRevision || isMilestone || isFinalExam) "Review entire vocabulary bank using SRS triggers." else "Learn 15 key academic and lifestyle words suited to level $level."
        val grammarGoals = if (isRevision || isMilestone || isFinalExam) "Consolidate active conjugation tables, cases and clauses." else "Study $level core components: Articles, Nominative/Accusative/Dative structures."

        val listeningTasks = if (isFinalExam) "B1 Standard Mock Listening section (40 minutes)." else "Listen to Nico's Weg video dialogue and transcribe main highlights."
        val speakingTasks = if (isFinalExam) "B1 Dual Conversation Simulation (2-person presentation)." else "Practice pronunciation using Voice Lab on targeted lesson verbs."
        val writingTasks = if (isFinalExam) "Compose formal complaint / formal university email request (150 words)." else "Add a custom journal entry about today's engineering or travel topics."
        val revisionTasks = "Run scheduled SRS spaced reviews (1, 3, 7, 14 days) on Vocab Notebook."

        RoadmapDay(
            dayIndex = day,
            dateString = dateString,
            title = title,
            level = level,
            lessonIds = lessonIds,
            objectives = objectives,
            vocabularyGoals = vocabGoals,
            grammarGoals = grammarGoals,
            listeningTasks = listeningTasks,
            speakingTasks = speakingTasks,
            writingTasks = writingTasks,
            revisionTasks = revisionTasks,
            estimatedStudyTimeMinutes = minutes,
            isRevisionDay = isRevision,
            isAssessmentDay = isAssessment,
            isMilestoneTest = isMilestone
        )
    }

    fun getLessonById(id: String): CurriculumLesson? {
        return lessons.find { it.id == id }
    }
}
