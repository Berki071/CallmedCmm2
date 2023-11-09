package com.medhelp.callmedcmm2.model.pasport_recognize

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RecognizeTextResponse {
    @SerialName("result")
    var result: Result? = null

    @Serializable
    class Result{
        @SerialName("textAnnotation")
        var textAnnotation: TextAnnotation? = null
        @SerialName("page")
        var page: String? = null

        @Serializable
        class TextAnnotation {
            @SerialName("width")
            var width: String? = null
            @SerialName("height")
            var height: String? = null
            @SerialName("blocks")
            var blocks: List<Blocks> = ArrayList<Blocks>()
            @SerialName("entities")
            var entities: List<Entities> = ArrayList<Entities>()

            @Serializable
            class Blocks{
                @SerialName("boundingBox")
                var boundingBox:  BoundingBox? = null
                @SerialName("lines")
                var lines: List<Lines> = ArrayList<Lines>()
                @SerialName("languages")
                var languages: List<Languages> = ArrayList<Languages>()

                @Serializable
                class BoundingBox{
                    @SerialName("vertices")
                    var vertices: List<Vertices> = ArrayList<Vertices>()

                    @Serializable
                    class Vertices {
                        @SerialName("x")
                        var x: String? = null
                        @SerialName("y")
                        var y: String? = null
                    }
                }
                @Serializable
                class Lines{
                    @SerialName("boundingBox")
                    var boundingBox:  BoundingBox? = null
                    @SerialName("text")
                    var text: String? = null
                    @SerialName("words")
                    var words: List<Words> = ArrayList<Words>()

                    @Serializable
                    class Words{
                        @SerialName("boundingBox")
                        var boundingBox:  BoundingBox? = null
                        @SerialName("text")
                        var text: String? = null
                        @SerialName("entityIndex")
                        var entityIndex: String? = null
                    }
                }
                @Serializable
                class Languages {
                    @SerialName("languageCode")
                    var languageCode: String? = null
                }
            }
            @Serializable
            class Entities{
                @SerialName("name")
                var name: String? = null
                @SerialName("text")
                var text: String? = null
            }
        }
    }
}