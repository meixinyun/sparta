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
package com.stratio.sparta.serving.api

import akka.event.slf4j.SLF4JLogging
import com.stratio.sparta.serving.api.helpers.SpartaHelper
import com.stratio.sparta.serving.core.config.SpartaConfig
import com.stratio.sparta.serving.core.constants.AppConstant

/**
 * Entry point of the application.
 */
object Sparta extends App with SLF4JLogging {

  SpartaConfig.initMainConfig()
  SpartaConfig.initDAOs
  SpartaConfig.initApiConfig()
  SpartaConfig.initSprayConfig()
  SpartaConfig.initSwaggerConfig()
  SpartaHelper.initAkkaSystem(AppConstant.ConfigAppName)
}
