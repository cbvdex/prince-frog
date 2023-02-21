package com.example.frogprince.businesslogic

import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AssessmentDetailModel
import com.example.frogprince.db.model.UserAnswerModel
import com.example.frogprince.db.model.UserModel
import com.example.frogprince.util.Utility
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList

class AssessmentAnalyzer(manager : DAOManager, user: UserModel){

    var manager : DAOManager = manager
    var user: UserModel = user

    fun produceAssessments(){

        if(user != null) {
            // delete existing assessments (to get a fresh new result)
            manager.assessmentDetailDAO.deleteAssessmentDetailByUser(user!!.userId)

            // produce new analysis for each category.
            produceAssessmentType1()
            produceAssessmentType2()
            produceAssessmentType3()
            produceAssessmentType4()
        }
    }

    fun produceAssessmentType1(){

        //update analysis
        var assessDetail : AssessmentDetailModel? = null
        var mainQuestionId : String = "1"
        var userAnswersMain1: ArrayList<UserAnswerModel>? = null
        var resultCate1 : String? = null

        var userAnswersSub1: ArrayList<UserAnswerModel>? = null
        var resAnswersSub1 : HashMap<String, Int> = HashMap<String, Int>()

        var userAnswersSub2: ArrayList<UserAnswerModel>? = null
        var resAnswersSub2 : HashMap<String, Int> = HashMap<String, Int>()

        if(user != null) {

            /** Analysis from Question #1-1, 1-2 **/
            userAnswersMain1 =
                manager.userAnswerDAO.findUserAnswersByUserByMainquestion(user!!.userId, "1")

            userAnswersSub1 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "1")

            userAnswersSub2 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "2")

            if(userAnswersMain1 != null && userAnswersSub1 != null
                && userAnswersMain1.size > 0 && userAnswersSub1.size > 0
                && (userAnswersSub1.size >= userAnswersSub2.size)
            ) {

                for (i in userAnswersSub1) {
                    var count: Int? = resAnswersSub1.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub1.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub1.put(i.answer.toString(), (count!! + 1))
                    }
                }
                resultCate1 = "The "+resAnswersSub1.size+" of "+userAnswersMain1.size +
                            " people treated you the way you like the most, such as: "

                for (key in resAnswersSub1.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub1.get(key).toString().toInt()) / (userAnswersSub1.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate1 += key + "(" + percentRounded + "%) "
                }
                Utility.println(resultCate1)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel("", user!!.userId, mainQuestionId,
                        "Which treatment you're seeking to feel in relationship from your partner?", resultCate1.toString())
                )

            }else if(userAnswersMain1 != null && userAnswersSub2 != null
                && userAnswersMain1.size > 0 && userAnswersSub2.size > 0
                && (userAnswersSub1.size < userAnswersSub2.size)
            ) {
                for (i in userAnswersSub2) {
                    var count: Int? = resAnswersSub2.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub2.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub2.put(i.answer.toString(), (count!! + 1))
                    }
                }
                resultCate1 = "The "+resAnswersSub2.size+" of "+userAnswersMain1.size +
                        " people presented the warning signs (you disliked the most), such as: "

                for (key in resAnswersSub2.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub2.get(key).toString().toInt()) / (userAnswersSub2.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate1 += key + "(" + percentRounded + "%) "
                }
                Utility.println(resultCate1)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel("", user!!.userId, mainQuestionId,
                        "Which attitude from your partner you should avoid in a relationship?", resultCate1.toString())
                )
            }

        }
    }

    fun produceAssessmentType2(){
        /** Analysis from Question #2-1, 2-2 **/

        var assessDetail: AssessmentDetailModel? = null

        var mainQuestionId: String = "2"
        var userAnswersMain2: ArrayList<UserAnswerModel>? = null

        var userAnswersSub3: ArrayList<UserAnswerModel>? = null
        var resAnswersSub3: HashMap<String, Int> = HashMap<String, Int>()

        var userAnswersSub4: ArrayList<UserAnswerModel>? = null
        var resAnswersSub4: HashMap<String, Int> = HashMap<String, Int>()

        var resultCate2: String? = null

        if (user != null) {

            /** Analysis from Main Question #2 **/
            userAnswersMain2 =
                manager.userAnswerDAO.findUserAnswersByUserByMainquestion(user!!.userId, "2")
            userAnswersSub3 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "3")
            userAnswersSub4 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "4")

            if(userAnswersMain2 != null && userAnswersSub3 != null
                && userAnswersMain2.size > 0 && userAnswersSub3.size > 0
                && (userAnswersSub3.size >= userAnswersSub4.size)
            ) {

                for (i in userAnswersSub3) {
                    var count: Int? = resAnswersSub3.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub3.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub3.put(i.answer.toString(), (count!! + 1))
                    }
                }
                var attractedPercentage: Double =
                    (100.0 * resAnswersSub3.size) / (userAnswersMain2.size)
                val attractedPercentRounded =
                    BigDecimal(attractedPercentage).setScale(0, RoundingMode.HALF_EVEN)
                resultCate2 =
                    "Your " + attractedPercentRounded + " % dates had your desirable features. You love  "
                for (key in resAnswersSub3.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub3.get(key).toString()
                            .toInt()) / (userAnswersSub3.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate2 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate2)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel(
                        "",
                        user!!.userId,
                        mainQuestionId,
                        "You love to date someone who has desired features (physique/personality).",
                        resultCate2.toString()
                    )
                )

            }else if (userAnswersMain2 != null && userAnswersSub4 != null
                && userAnswersMain2.size > 0 && userAnswersSub4.size > 0
                && (userAnswersSub3.size < userAnswersSub4.size)
            ) {

                for (i in userAnswersSub4) {
                    var count: Int? = resAnswersSub4.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub4.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub4.put(i.answer.toString(), (count!! + 1))
                    }
                }
                var percentage: Double =
                    (100.0 * resAnswersSub4.size) / (userAnswersMain2.size)
                val percentRounded =
                    BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                resultCate2 =
                    "Your " + percentRounded + " % dates had disappointed you with their feature or personality, such as:  "
                for (key in resAnswersSub4.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub4.get(key).toString()
                            .toInt()) / (userAnswersSub4.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate2 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate2)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel(
                        "",
                        user!!.userId,
                        mainQuestionId,
                        "What traits you should watch out for when starting a relationship ?",
                        resultCate2.toString()
                    )
                )
            }

        }
    }

    fun produceAssessmentType3(){

        //update analysis
        var assessDetail : AssessmentDetailModel? = null
        var mainQuestionId : String = "3"
        var userAnswersMain3: ArrayList<UserAnswerModel>? = null
        var resultCate3 : String? = null

        var userAnswersSub5: ArrayList<UserAnswerModel>? = null
        var resAnswersSub5 : HashMap<String, Int> = HashMap<String, Int>()

        var userAnswersSub6: ArrayList<UserAnswerModel>? = null
        var resAnswersSub6 : HashMap<String, Int> = HashMap<String, Int>()

        if(user != null) {

            /** Analysis from Question #3-1, 3-2 **/
            userAnswersMain3 =
                manager.userAnswerDAO.findUserAnswersByUserByMainquestion(user!!.userId, "3")

            userAnswersSub5 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "5")

            userAnswersSub6 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "6")

            if(userAnswersMain3 != null && userAnswersSub5 != null
                && userAnswersMain3.size > 0 && userAnswersSub5.size > 0
                && (userAnswersSub5.size >= userAnswersSub6.size)
            ) {

                for (i in userAnswersSub5) {
                    var count: Int? = resAnswersSub5.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub5.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub5.put(i.answer.toString(), (count!! + 1))
                    }
                }
                resultCate3 = "The " + resAnswersSub5.size + " out of " + userAnswersMain3.size +
                        " people had the supporting personalities for you, which are "

                for (key in resAnswersSub5.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub5.get(key).toString()
                            .toInt()) / (userAnswersSub5.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate3 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate3)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel(
                        "",
                        user!!.userId,
                        mainQuestionId,
                        "What are the harmonizing personality of your date to support your personality?.",
                        resultCate3.toString()
                    )
                )

            }else if(userAnswersMain3 != null && userAnswersSub6 != null
                && userAnswersMain3.size > 0 && userAnswersSub6.size > 0
                && (userAnswersSub5.size < userAnswersSub6.size)
            ) {

                for (i in userAnswersSub6) {
                    var count: Int? = resAnswersSub6.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub6.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub6.put(i.answer.toString(), (count!! + 1))
                    }
                }
                resultCate3 = "The "+resAnswersSub6.size+" out of "+userAnswersMain3.size +
                        " people had ruined relationship that does not get along with you by being "

                for (key in resAnswersSub6.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub6.get(key).toString().toInt()) / (userAnswersSub6.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate3 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate3)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel("", user!!.userId, mainQuestionId,
                        "Remember your deal breaker (the ruining personality) for a relationship!", resultCate3.toString())
                )
            }

        }
    }

    fun produceAssessmentType4() {

        /** Analysis from Question #4-1, 4-2 **/

        var assessDetail: AssessmentDetailModel? = null

        var mainQuestionId: String = "4"
        var userAnswersMain4: ArrayList<UserAnswerModel>? = null

        var userAnswersSub7: ArrayList<UserAnswerModel>? = null
        var resAnswersSub7: HashMap<String, Int> = HashMap<String, Int>()

        var userAnswersSub8: ArrayList<UserAnswerModel>? = null
        var resAnswersSub8: HashMap<String, Int> = HashMap<String, Int>()

        var resultCate4: String? = null

        if (user != null) {

            /** Analysis from Main Question #4 **/
            userAnswersMain4 =
                manager.userAnswerDAO.findUserAnswersByUserByMainquestion(user!!.userId, "4")
            userAnswersSub7 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "7")
            userAnswersSub8 =
                manager.userAnswerDAO.findUserAnswersByUserBySubquestion(user!!.userId, "8")

            if(userAnswersMain4 != null && userAnswersSub7 != null
                && userAnswersMain4.size > 0 && userAnswersSub7.size > 0
                && (userAnswersSub7.size >= userAnswersSub8.size)
            ) {

                for (i in userAnswersSub7) {
                    var count: Int? = resAnswersSub7.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub7.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub7.put(i.answer.toString(), (count!! + 1))
                    }
                }
                var attractedPercentage: Double =
                    (100.0 * resAnswersSub7.size) / (userAnswersMain4.size)
                val attractedPercentRounded =
                    BigDecimal(attractedPercentage).setScale(0, RoundingMode.HALF_EVEN)
                resultCate4 = "You dated "+attractedPercentRounded+" % of people out of attractiveness. The most appealing factors were "
                for (key in resAnswersSub7.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub7.get(key).toString().toInt()) / (userAnswersSub7.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate4 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate4)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel("", user!!.userId, mainQuestionId,
                        "How much /what kinds of attractiveness is important in your relationship?", resultCate4.toString())
                )
            }else if (userAnswersMain4 != null && userAnswersSub8 != null
                && userAnswersMain4.size > 0 && userAnswersSub8.size > 0
                && (userAnswersSub7.size < userAnswersSub8.size)
            ) {

                for (i in userAnswersSub8) {
                    var count: Int? = resAnswersSub8.get(i.answer.toString())
                    if (count == null) {
                        resAnswersSub8.put(i.answer.toString(), 1)
                    } else {
                        resAnswersSub8.put(i.answer.toString(), (count!! + 1))
                    }
                }
                var percentage: Double =
                    (100.0 * resAnswersSub8.size) / (userAnswersMain4.size)
                val percentRounded =
                    BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                resultCate4 =
                    "You gave a chance to " + percentRounded + " % of people though they were not your type. The appealing factors were "
                for (key in resAnswersSub8.keys) {
                    var percentage: Double =
                        (100.0 * resAnswersSub8.get(key).toString()
                            .toInt()) / (userAnswersSub8.size)
                    val percentRounded =
                        BigDecimal(percentage).setScale(0, RoundingMode.HALF_EVEN)
                    resultCate4 += key + " (" + percentRounded + "%) "
                }
                Utility.println(resultCate4)
                manager.assessmentDetailDAO.insertAssessment(
                    AssessmentDetailModel(
                        "",
                        user!!.userId,
                        mainQuestionId,
                        "What traits of the other person makes you take a risk (not your type, but give a try)?",
                        resultCate4.toString()
                    )
                )
            }
        }
    }
}