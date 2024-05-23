/*
 * Copyright (c) 2023-2024 Works Applications Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worksap.nlp.elasticsearch.sudachi.index

import org.junit.Rule
import org.junit.Test

class CustomMultiFilterAnalyzerTest : SearchEngineTestBase {
  @JvmField @Rule var engine = SearchEngineEnv()

  @Test
  fun baseform_readingform() {
    val settings =
        """
      {
        "index.analysis": {
          "analyzer": {
            "sudachi_test": {
              "type": "custom",
              "tokenizer": "sudachi_tokenizer",
              "filter": ["sudachi_baseform", "sudachi_readingform"]
            }
          },
          "tokenizer": {
            "sudachi_tokenizer": {
              "type": "sudachi_tokenizer",
              "split_mode": "C"
            }
          }
        }
      }
    """.jsonSettings()
    val analyzers = engine.indexAnalyzers(settings)
    val basic = analyzers.get("sudachi_test")
    basic.assertTerms("東京に行った", "トウキョウ", "ニ", "イッ", "タ")
  }

  @Test
  fun stopward_baseform() {
    val settings =
        """
      {
        "index.analysis": {
          "analyzer": {
            "sudachi_test": {
              "type": "custom",
              "tokenizer": "sudachi_tokenizer",
              "filter": ["stop", "sudachi_baseform"]
            }
          },
          "tokenizer": {
            "sudachi_tokenizer": {
              "type": "sudachi_tokenizer",
              "split_mode": "C"
            }
          },
          "filter": {
            "stop": {
              "type": "sudachi_ja_stop",
              "stopwords": ["に", "行く"]
            }            
          }
        }
      }
    """.jsonSettings()
    val analyzers = engine.indexAnalyzers(settings)
    val basic = analyzers.get("sudachi_test")
    basic.assertTerms("東京に行った", "東京", "行く", "た")
  }

  @Test
  fun baseform_stopward() {
    val settings =
        """
      {
        "index.analysis": {
          "analyzer": {
            "sudachi_test": {
              "type": "custom",
              "tokenizer": "sudachi_tokenizer",
              "filter": ["sudachi_baseform", "stop"]
            }
          },
          "tokenizer": {
            "sudachi_tokenizer": {
              "type": "sudachi_tokenizer",
              "split_mode": "C"
            }
          },
          "filter": {
            "stop": {
              "type": "sudachi_ja_stop",
              "stopwords": ["に", "行く"]
            }            
          }
        }
      }
    """.jsonSettings()
    val analyzers = engine.indexAnalyzers(settings)
    val basic = analyzers.get("sudachi_test")
    basic.assertTerms("東京に行った", "東京", "た")
  }
}
