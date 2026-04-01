import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class NewTicketTask : DefaultTask() {
    @get:InputDirectory
    abstract val docsDir: DirectoryProperty

    @get:Input
    abstract val ticket: Property<String>

    @get:Input
    abstract val title: Property<String>

    @TaskAction
    fun generate() {
        val normalizedTicket = ticket.orNull?.trim().orEmpty()
        val normalizedTitle = title.orNull?.trim().orEmpty()

        require(normalizedTicket.isNotBlank()) {
            "Property 'ticket' is required. Example: ./gradlew newTicket -Pticket=MAP-031 -Ptitle=\"Refactor MapScreenOverlays by responsibility\""
        }
        require(normalizedTitle.isNotBlank()) {
            "Property 'title' is required. Example: ./gradlew newTicket -Pticket=MAP-031 -Ptitle=\"Refactor MapScreenOverlays by responsibility\""
        }
        require(Regex("[A-Z]+-\\d+").matches(normalizedTicket)) {
            "Property 'ticket' must match pattern [A-Z]+-\\d+, got: $normalizedTicket"
        }

        val docsRoot = docsDir.asFile.get()
        val activeTicketFile = docsRoot.resolve(".active_ticket")
        val prdTemplate = docsRoot.resolve("prd.template.md")
        val planTemplate = docsRoot.resolve("plan.template.md")
        val tasklistTemplate = docsRoot.resolve("tasklist.template.md")

        require(docsRoot.exists()) { "Missing docs directory: ${docsRoot.path}" }
        require(activeTicketFile.exists()) { "Missing AGENTS.md required file: ${activeTicketFile.path}" }
        require(prdTemplate.exists()) { "Missing template: ${prdTemplate.path}" }
        require(planTemplate.exists()) { "Missing template: ${planTemplate.path}" }
        require(tasklistTemplate.exists()) { "Missing template: ${tasklistTemplate.path}" }

        val prdDir = docsRoot.resolve("prd")
        val planDir = docsRoot.resolve("plan")
        val tasklistDir = docsRoot.resolve("tasklist")

        val prdFile = prdDir.resolve("$normalizedTicket.prd.md")
        val planFile = planDir.resolve("$normalizedTicket.md")
        val tasklistFile = tasklistDir.resolve("$normalizedTicket.md")

        require(!prdFile.exists()) { "File already exists: ${prdFile.path}" }
        require(!planFile.exists()) { "File already exists: ${planFile.path}" }
        require(!tasklistFile.exists()) { "File already exists: ${tasklistFile.path}" }

        fun File.readUtf8(): String = readText(Charsets.UTF_8)
        fun File.writeUtf8(text: String) = writeText(text, Charsets.UTF_8)

        val prdContent = prdTemplate
            .readUtf8()
            .replace("<ticket>", normalizedTicket)
            .replace("<short title>", normalizedTitle)

        val planContent = planTemplate
            .readUtf8()
            .replace("<ticket>", normalizedTicket)

        val tasklistContent = tasklistTemplate
            .readUtf8()
            .replace("<ticket>", normalizedTicket)

        prdDir.mkdirs()
        planDir.mkdirs()
        tasklistDir.mkdirs()

        prdFile.writeUtf8(prdContent)
        planFile.writeUtf8(planContent)
        tasklistFile.writeUtf8(tasklistContent)
        activeTicketFile.writeUtf8("$normalizedTicket\n")

        println("AGENTS.md workflow prepared for ticket: $normalizedTicket")
        println("Created:")
        println("- ${prdFile.path}")
        println("- ${planFile.path}")
        println("- ${tasklistFile.path}")
        println("Updated:")
        println("- ${activeTicketFile.path} -> $normalizedTicket")
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        source.setFrom(files("src/commonMain/kotlin", "src/androidMain/kotlin", "src/jvmMain/kotlin", "src/commonTest/kotlin"))
    }
}

tasks.register<NewTicketTask>("newTicket") {
    group = "documentation"
    description = "Creates ticket docs required by AGENTS.md and updates docs/.active_ticket"
    docsDir.convention(layout.projectDirectory.dir("docs"))
    ticket.convention(providers.gradleProperty("ticket"))
    title.convention(providers.gradleProperty("title"))
}
