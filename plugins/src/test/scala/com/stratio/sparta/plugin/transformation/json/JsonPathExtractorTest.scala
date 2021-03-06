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

package com.stratio.sparta.plugin.transformation.json

import org.scalatest._
import org.scalatest.mock.MockitoSugar

class JsonPathExtractorTest extends FlatSpec with ShouldMatchers with MockitoSugar {

  val JSON = """{ "store": {
              |    "book": [
              |      { "category": "reference",
              |        "author": "Nigel Rees",
              |        "title": "Sayings of the Century",
              |        "price": 8.95
              |      },
              |      { "category": "fiction",
              |        "author": "Evelyn Waugh",
              |        "title": "Sword of Honour",
              |        "price": 12.99
              |      },
              |      { "category": "fiction",
              |        "author": "Herman Melville",
              |        "title": "Moby Dick",
              |        "isbn": "0-553-21311-3",
              |        "price": 8.99
              |      },
              |      { "category": "fiction",
              |        "author": "J. R. R. Tolkien",
              |        "title": "The Lord of the Rings",
              |        "isbn": "0-395-19395-8",
              |        "price": 22.99
              |      }
              |    ],
              |    "bicycle": {
              |      "color": "red",
              |      "price": 19.95
              |    }
              |  }
              |}""".stripMargin


  it should "return bicycle color with dot-notation query" in {
    val query = "$.store.bicycle.color"

    val result = new JsonPathExtractor(JSON).query(query)

    result.asInstanceOf[String] should be("red")
  }

  it should "return bicycle color with bracket-notation query" in {
    val query = "$['store']['bicycle']['color']"

    val result = new JsonPathExtractor(JSON).query(query)

    result.asInstanceOf[String] should be("red")
  }

  it should "return bicycle price" in {
    val query = "$.store.bicycle.price"

    val result = new JsonPathExtractor(JSON).query(query)

    result.asInstanceOf[Double] should be(19.95)
  }
}
