@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER") @file:OptIn(DelicateKotlinPoetApi::class)

package dev.schlaubi.lavakord.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.PlayOptions
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.internal.QueryBuilder
import dev.schlaubi.lavakord.ksp.generator.generateBuilder
import dev.schlaubi.lavakord.ksp.generator.generateBuilderFunction
import dev.schlaubi.lavakord.ksp.generator.search
import dev.schlaubi.lavakord.ksp.generator.searchAndPlay
import dev.schlaubi.lavakord.rest.loadItem
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind

private val CONTRACT = MemberName("kotlin.contracts", "contract")

@OptIn(ExperimentalContracts::class)
private val EXACTLY_ONCE = MemberName(InvocationKind::class.asClassName(), "EXACTLY_ONCE")
private val PLAY_OPTIONS = typeNameOf<PlayOptions>()
internal val PLAY_OPTIONS_BUILDER = LambdaTypeName.get(PLAY_OPTIONS, returnType = UNIT)
internal val QUERY_BUILDER = typeNameOf<QueryBuilder>()
internal val loadItem = Node::loadItem.asMemberName()

internal val GenerateQueryHelper.functionName: String
    get() = serviceName.replace("\\s+".toRegex(), "")

@OptIn(DelicateKotlinPoetApi::class)
internal fun generateHelpers(
    name: String,
    packageName: String,
    children: List<GenerateQueryHelper>,
    environment: SymbolProcessorEnvironment,
    originatingFile: KSFile
) {
    val classFile = ClassName(packageName, "${name}HelperFunctions")
    val file = FileSpec.builder(classFile).apply {
        indent(" ".repeat(4))
        addAnnotation(AnnotationSpec.get(Suppress("IncorrectFormatting", "INVISIBLE_REFERENCE")))
        addFileComment("DO NOT EDIT!! - This code has been generated by QueryUtilityProcessor\n")
        addFileComment("Edit this file instead ${originatingFile.filePath.substringAfter("plugins/")}")
        children.forEach {
            it.generateHelpers(this)
        }
    }.build()

    file.writeTo(environment.codeGenerator, true, listOf(originatingFile))
}

private fun GenerateQueryHelper.generateHelpers(addTo: FileSpec.Builder) {
    val builderName = ClassName(packageName, "${functionName}${operationName.capitalize()}QueryBuilder")

    with(addTo) {
        addKotlinDefaultImports(includeJvm = false, includeJs = false)
        if(generateSearchAndPlayFunction) {
            addFunction(searchAndPlay(builderName))
        }
        addFunction(search(builderName))
        if (builderOptions.isNotEmpty()) {
            addType(generateBuilder(builderName))
            addFunction(generateBuilderFunction(builderName))
        }
    }
}

internal fun GenerateQueryHelper.generateFunction(
    name: String, builderParameterName: String, builder: FunSpec.Builder.(QueryFunctionContext) -> Unit
): FunSpec = FunSpec.builder(name).apply {
    this@generateFunction.parameters.forEach { parameter ->
        val spec = ParameterSpec.builder(parameter.name, parameter.type.toType()).addKdoc(parameter.kDoc).build()
        addParameter(spec)
    }
    val queryString = buildParameters(builderParameterName)
    builder(QueryFunctionContext(queryString))
}.build()

internal fun GenerateQueryHelper.buildParameters(builderParameterName: String): CodeBlock {
    return if (builderOptions.isNotEmpty()) {
        buildBuilderParameters(builderParameterName)
    } else {
        buildStringParameters()
    }
}

internal fun GenerateQueryHelper.buildBuilderParameters(builderParameterName: String): CodeBlock {
    val parameters = this@GenerateQueryHelper.parameters.map {
        CodeBlock.of("""%L""", it.name)
    }.joinToCode()
    return CodeBlock.of("""%S + %L.toQuery(%L)""", "$prefix:", builderParameterName, parameters)
}

internal fun GenerateQueryHelper.buildStringParameters(): CodeBlock {
    var isFirstParam = true
    val prefix = CodeBlock.of("%L:", prefix)
    val parameters = listOf(prefix) + parameters.map { parameter ->
        if (parameter.queryName.isNotEmpty()) {
            val seperator = if (isFirstParam) {
                isFirstParam = false
                "?"
            } else {
                "&"
            }
            CodeBlock.of("""$seperator%L=$%N""", parameter.queryName, parameter.name)
        } else {
            CodeBlock.of("""$%N""", parameter.name)
        }
    }

    return parameters.joinToCode("", "\"", "\"")
}

internal fun FunSpec.Builder.addBuilderContract(builderParameter: ParameterSpec) = apply {
    addStatement("%M { callsInPlace(%N, %M) }", CONTRACT, builderParameter, EXACTLY_ONCE)
}

internal class QueryFunctionContext(val queryString: CodeBlock)

