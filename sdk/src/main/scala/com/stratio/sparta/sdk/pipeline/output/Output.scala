/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.sparta.sdk.pipeline.output

import java.io.{Serializable => JSerializable}

import com.stratio.sparta.sdk.pipeline.schema.TypeOp.TypeOp
import com.stratio.sparta.sdk.properties.Parameterizable
import com.stratio.sparta.sdk.pipeline.schema.{SpartaSchema, TypeOp}
import org.apache.spark.Logging
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SaveMode}

abstract class Output(keyName: String,
                      version: Option[Int],
                      properties: Map[String, JSerializable],
                      schemas: Seq[SpartaSchema])
  extends Parameterizable(properties) with Logging {

  def name: String = keyName

  def setup(options: Map[String, String] = Map.empty[String, String]): Unit = {}

  def save(dataFrame: DataFrame, saveMode: SaveModeEnum.Value, options: Map[String, String]): Unit

  def versionedTableName(tableName: String): String = {
    val versionChain = version match {
      case Some(v) => s"${Output.Separator}v$v"
      case None => ""
    }
    s"$tableName$versionChain"
  }

  def supportedSaveModes : Seq[SaveModeEnum.Value] = SaveModeEnum.allSaveModes

  def validateSaveMode(saveMode: SaveModeEnum.Value): Unit = {
    if(!supportedSaveModes.contains(saveMode))
      log.info(s"Save mode $saveMode selected not supported by the output $name." +
          s" Using the default mode ${SaveModeEnum.Append}"
      )
  }
}

object Output extends Logging {

  final val ClassSuffix = "Output"
  final val Separator = "_"
  final val FieldsSeparator = ","
  final val TableNameKey = "tableName"
  final val TimeDimensionKey = "timeDimension"
  final val MeasureMetadataKey = "measure"
  final val PrimaryKeyMetadataKey = "pk"

  def getSparkSaveMode(saveModeEnum: SaveModeEnum.Value) : SaveMode =
    saveModeEnum match {
      case SaveModeEnum.Append => SaveMode.Append
      case SaveModeEnum.ErrorIfExists => SaveMode.ErrorIfExists
      case SaveModeEnum.Overwrite => SaveMode.Overwrite
      case SaveModeEnum.Ignore => SaveMode.Ignore
      case SaveModeEnum.Upsert => SaveMode.Append
      case _ =>
        log.info(s"Save Mode $saveModeEnum not supported, using default save mode ${SaveModeEnum.Append}")
        SaveMode.Append
    }

  def getTimeFromOptions(options: Map[String, String]): Option[String] = options.get(TimeDimensionKey)

  def getTableNameFromOptions(options: Map[String, String]): String =
    options.getOrElse(TableNameKey, {
      log.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")
    })

  def getTimeFieldType(dateTimeType: TypeOp,
                       fieldName: String,
                       nullable: Boolean,
                       metadata: Option[Metadata] = None): StructField =
    dateTimeType match {
      case TypeOp.Date | TypeOp.DateTime => defaultDateField(fieldName, nullable, metadata.getOrElse(Metadata.empty))
      case TypeOp.Timestamp => defaultTimeStampField(fieldName, nullable, metadata.getOrElse(Metadata.empty))
      case TypeOp.Long => defaultLongField(fieldName, nullable, metadata.getOrElse(Metadata.empty))
      case TypeOp.String => defaultStringField(fieldName, nullable, metadata.getOrElse(Metadata.empty))
      case _ => defaultStringField(fieldName, nullable, metadata.getOrElse(Metadata.empty))
    }

  def defaultTimeStampField(fieldName: String, nullable: Boolean, metadata: Metadata = Metadata.empty): StructField =
    StructField(fieldName, TimestampType, nullable, metadata)

  def defaultDateField(fieldName: String, nullable: Boolean, metadata: Metadata = Metadata.empty): StructField =
    StructField(fieldName, DateType, nullable, metadata)

  def defaultStringField(fieldName: String, nullable: Boolean, metadata: Metadata = Metadata.empty): StructField =
    StructField(fieldName, StringType, nullable, metadata)

  def defaultGeoField(fieldName: String, nullable: Boolean, metadata: Metadata = Metadata.empty): StructField =
    StructField(fieldName, ArrayType(DoubleType), nullable, metadata)

  def defaultLongField(fieldName: String, nullable: Boolean, metadata: Metadata = Metadata.empty): StructField =
    StructField(fieldName, LongType, nullable, metadata)
}