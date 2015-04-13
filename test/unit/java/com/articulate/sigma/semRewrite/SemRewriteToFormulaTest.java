package com.articulate.sigma.semRewrite;

import com.articulate.sigma.Formula;
import com.articulate.sigma.UnitTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

/**
 * Tests the SemRewrite of a given parse, plus its manipulation into a formula string.
 * In: Dependency parse as string.
 * Out: a formula string.
 */
public class SemRewriteToFormulaTest extends UnitTestBase {

    private static Interpreter interpreter;

    @Before
    public void setUpInterpreter()  {
        interpreter = new Interpreter();
        interpreter.inference = false;
        interpreter.initialize();
    }

    /***************************************************************
     * Mary is at the house.
     * prep_at(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (located(?X,?Y))
     */
    @Test
    public void testMaryAtHouse() {
        String input = "root(ROOT-0, be-2), nsubj(be-2, Mary-1), det(house-5, the-4), prep_at(be-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, be-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?be-2 ?Mary-1 ?house-5) \n" +
                "(and \n" +
                "  (attribute ?Mary-1 Female)\n" +
                "  (located ?be-2 ?house-5)\n" +
                "  (names ?Mary-1 \"Mary\")\n" +
                "  (instance ?house-5 House)\n" +
                "  (instance ?Mary-1 Human))\n" +
                ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary is in the house.
     * prep_in(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Inside)}.
     */
    @Test
    public void testMaryInHouse() {
        String input = "root(ROOT-0, be-2), nsubj(be-2, Mary-1), det(house-5, the-4), prep_in(be-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, be-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?be-2 ?Mary-1 ?house-5) \n" +
                        "(and \n" +
                        "  (orientation ?be-2 ?house-5 Inside)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary is inside the house.
     * prep_inside(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Inside)}.
     */
    @Test
    public void testMaryInsideHouse() {
        String input = "root(ROOT-0, be-2), nsubj(be-2, Mary-1), det(house-5, the-4), prep_inside(be-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, be-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?be-2 ?Mary-1 ?house-5) \n" +
                        "(and \n" +
                        "  (orientation ?be-2 ?house-5 Inside)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary is outside the house.
     * prep_outside(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Outside)}.
     */
    @Test
    public void testMaryOutsideHouse() {
        String input = "root(ROOT-0, be-2), nsubj(be-2, Mary-1), det(house-5, the-4), prep_outside(be-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, be-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?be-2 ?Mary-1 ?house-5) \n" +
                        "(and \n" +
                        "  (orientation ?be-2 ?house-5 Outside)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary is on the house.
     * prep_on(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (located(?X,?Y)).
     */
    @Test
    public void testMaryOnHouse() {
        String input = "det(house-5, the-4), root(ROOT-0, be-2), nsubj(be-2, Mary-1), prep_on(be-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, be-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?be-2 ?Mary-1 ?house-5) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (located ?be-2 ?house-5)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary goes to the house.
     * prep_to(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (destination(?X,?Y)).
     */
    @Test
    public void testMaryGoToHouse() {
        String input = "det(house-5, the-4), root(ROOT-0, go-2), nsubj(go-2, Mary-1), prep_to(go-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, go-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?go-2 ?house-5) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (destination ?go-2 ?house-5)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary goes towards the house.
     * prep_towards(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (destination(?X,?Y)).
     */
    @Test
    public void testMaryGoTowardsHouse() {
        String input = "root(ROOT-0, go-2), nsubj(go-2, Mary-1), det(house-5, the-4), prep_toward(go-2, house-5), sumo(House, house-5), names(Mary-1, \"Mary\"), sumo(Transportation, go-2), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, go-2), number(SINGULAR, house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?go-2 ?Mary-1 ?house-5) \n" +
                        "(and \n" +
                        "  (agent ?go-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (destination ?go-2 ?house-5)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?go-2 Transportation)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }


    /** *************************************************************
     * Mary goes in an hour.
     * prep_in(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (time(?X,?Y)).
     */
    @Test
    public void testMaryGoInHour() {
        String input = "det(hour-5, a-4), root(ROOT-0, go-2), nsubj(go-2, Mary-1), prep_in(go-2, hour-5), names(Mary-1, \"Mary\"), sumo(Hour, hour-5), attribute(Mary-1, Female), sumo(Human, Mary-1), number(SINGULAR, Mary-1), tense(PRESENT, go-2), number(SINGULAR, hour-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?go-2 ?hour-5) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (time ?go-2 ?hour-5)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary goes on Tuesday.
     * prep_on(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (time(?X,?Y)).
     */
    @Test
    public void testMaryGoOnTuesday() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_on(go-2,Tuesday-4), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PRESENT,go-2), number(SINGULAR,Tuesday-4), day(time-1,Tuesday), time(goesOn-2,time-1)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?go-2 ?Mary-1 ?Y ?M) \n" +
                        "(and \n" +
                        "  (time ?go-2\n" +
                        "  (DayFn Tuesday\n" +
                        "    (MonthFn ?M\n" +
                        "      (YearFn ?Y))))\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walks for two hours.
     * prep_for(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Person) ==> (destination(?X,?Y)).
     */
    @Test
    public void testMaryWalkForTwoHours() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), num(hour-5,two-4), prep_for(walk-2,hour-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Hour,hour-5), sumo(Walking,walk-2), number(SINGULAR,Mary-1), tense(PRESENT,walk-2), number(PLURAL,hour-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?hour-5 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (duration ?walk-2 ?hour-5)\n" +
                        "  (instance ?hour-5 Collection)\n" +
                        "  (membersType ?hour-5 Hour)\n" +
                        "  (membersCount ?hour-5 2)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-2 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary has walked since Tuesday.
     * prep_since(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (starts(?Y,?X)).
     */
    @Test
    public void testMaryWalkSinceTuesday() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,have-2), prep_since(walk-3,Tuesday-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-3), number(SINGULAR,Mary-1), tense(PRESENT,walk-3), aspect(PERFECT,walk-3), number(SINGULAR,Tuesday-5), time(walk-3,time-1), day(time-1,Tuesday)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?Y ?walk-3 ?M) \n" +
                        "(and \n" +
                        "  (time ?walk-3\n" +
                        "  (DayFn Tuesday\n" +
                        "    (MonthFn ?M\n" +
                        "      (YearFn ?Y))))\n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-3 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walked through the day.
     * prep_through(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (duration(?X,?Y)).
     */
    @Test
    public void testMaryWalkThroughDay() {
        String input = "det(day-5,the-4), root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), prep_through(walk-2,day-5), names(Mary-1,\"Mary\"), sumo(Day,day-5), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,walk-2), number(SINGULAR,day-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?day-5 ?walk-2) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (duration ?walk-2 ?day-5)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walked through the house.
     * prep_through(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (traverses(?X,?Y)).
     */
    @Test
    public void testMaryWalkThroughHouse() {
        String input = "det(house-5,the-4), root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), prep_through(walk-2,house-5), sumo(House,house-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,walk-2), number(SINGULAR,house-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?house-5 ?walk-2) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (traverses ?walk-2 ?house-5)\n" +
                        "  (instance ?house-5 House)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walks with John.
     * prep_with(?X,?Y), +sumo(Human,?Y) ==> (agent(?X,?Y)).
     */
    @Test
    public void testMaryWalkWithJohn() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), prep_with(walk-2,John-4), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), attribute(John-4,Male), sumo(Human,Mary-1), sumo(Human,John-4), names(John-4,\"John\"), sumo(Walking,walk-2), number(SINGULAR,Mary-1), tense(PRESENT,walk-2), number(SINGULAR,John-4)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?John-4 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?John-4)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (attribute ?John-4 Male)\n" +
                        "  (names ?John-4 \"John\")\n" +
                        "  (instance ?John-4 Human)\n" +
                        "  (instance ?walk-2 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walks with the man.
     * prep_with(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Person) ==> (agent(?X,?Y)).
     */
    @Test
    public void testMaryWalkWithMan() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), det(man-5,the-4), prep_with(walk-2,man-5), sumo(Man,man-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-2), number(SINGULAR,Mary-1), tense(PRESENT,walk-2), number(SINGULAR,man-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?man-5 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?man-5)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?man-5 Man)\n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-2 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walks with the artifact.
     * prep_with(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Person) ==> (agent(?X,?Y)).
     */
    @Test
    public void testMaryWalkWithArtifact() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), det(artifact-5,the-4), prep_with(walk-2,artifact-5), sumo(Artifact,artifact-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-2), number(SINGULAR,Mary-1), tense(PRESENT,walk-2), number(SINGULAR,artifact-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?artifact-5 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (instrument ?walk-2 ?artifact-5)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?artifact-5 Artifact)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-2 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary went across the room.
     * prep_across(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (traverses(?X,?Y)).
     */
    @Test
    public void testMaryGoAcrossRoom() {
        String input = "det(room-5,the-4), root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_across(go-2,room-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Room,room-5), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,room-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?room-5 ?Mary-1 ?go-2) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (traverses ?go-2 ?room-5)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?room-5 Room))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walked within the room.
     * prep_within(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (properlyFills(?X,?Y)).
     */
    @Test
    public void testMaryWalkWithinRoom() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), det(room-5,the-4), prep_within(walk-2,room-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-2), sumo(Room,room-5), number(SINGULAR,Mary-1), tense(PAST,walk-2), number(SINGULAR,room-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?room-5 ?Mary-1 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (properlyFills ?walk-2 ?room-5)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-2 Walking)\n" +
                        "  (instance ?room-5 Room))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary walked into the room.
     * prep_into(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (properlyFills(?X,?Y)).
     */
    @Test
    public void testMaryWalkIntoRoom() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), det(room-5,the-4), prep_into(walk-2,room-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-2), sumo(Room,room-5), number(SINGULAR,Mary-1), tense(PAST,walk-2), number(SINGULAR,room-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?room-5 ?Mary-1 ?walk-2) \n" +
                        "(and \n" +
                        "  (agent ?walk-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (properlyFills ?walk-2 ?room-5)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-2 Walking)\n" +
                        "  (instance ?room-5 Room))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary will walk from the house.
     * prep_from(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> (origin(?X,?Y)).
     */
    @Test
    public void testMaryWillWalkFromHouse() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,will-2), det(house-6,the-5), prep_from(walk-3,house-6), sumo(House,house-6), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-3), number(SINGULAR,Mary-1), tense(FUTURE,walk-3), number(SINGULAR,house-6)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?house-6 ?walk-3) \n" +
                        "(and \n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (origin ?walk-3 ?house-6)\n" +
                        "  (earlier Now\n" +
                        "  (WhenFn ?walk-3))\n" +
                        "  (instance ?house-6 House)\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-3 Walking))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary was walking from noon.
     * prep_from(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (BeginFn(?X,?Y)).
     * This rule is triggered by this test's input, though the BeginFn does not at this time appear in the output.
     */
    @Test
    public void testMaryWasWalkingFromNoon() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,be-2), prep_from(walk-3,noon-5), names(Mary-1,\"Mary\"), sumo(Walking,walk-3), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(TimePosition,noon-5), number(SINGULAR,Mary-1), tense(PAST,walk-3), aspect(PROGRESSIVE,walk-3), number(SINGULAR,noon-5), time(walk-3,time-1), hour(time-1,12-5), minute(time-1,00-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?time-1 ?walk-3) \n" +
                        "(and \n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (time ?walk-3 ?time-1)\n" +
                        "  (instance ?walk-3 Walking)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary was walking until midnight.
     * prep_until(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (EndFn(?X,?Y)).
     * This rule is triggered by this test's input, though the EndFn does not at this time appear in the output.
     */
    @Test
    public void testMaryWasWalkingUntilMidnight() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,be-2), prep_until(walk-3,midnight-5), names(Mary-1,\"Mary\"), sumo(Walking,walk-3), sumo(TimePoint,midnight-5), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,walk-3), aspect(PROGRESSIVE,walk-3), number(SINGULAR,midnight-5), time(walk-3,time-1), hour(time-1,00-5), minute(time-1,00-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?time-1 ?walk-3) \n" +
                        "(and \n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (time ?walk-3 ?time-1)\n" +
                        "  (instance ?walk-3 Walking)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary was walking from noon until midnight.
     * prep_from(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (BeginFn(?X,?Y)).
     * prep_until(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (EndFn(?X,?Y)).
     * These two rules are triggered by this test's input, though the BeginFn and the EndFn do not at this time appear in the output.
     */
    @Test
    public void testMaryWasWalkingFromNoonUntilMidnight() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,be-2), prep_from(walk-3,noon-5), prep_until(walk-3,midnight-7), sumo(TimePoint,midnight-7), names(Mary-1,\"Mary\"), sumo(Walking,walk-3), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(TimePosition,noon-5), number(SINGULAR,Mary-1), tense(PAST,walk-3), aspect(PROGRESSIVE,walk-3), number(SINGULAR,noon-5), number(SINGULAR,midnight-7), time(walk-3,time-1), time(walk-3,time-2), hour(time-1,12-5), minute(time-2,00-7), hour(time-2,00-7), minute(time-1,00-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?time-2 ?time-1 ?walk-3) \n" +
                        "(and \n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (time ?walk-3 ?time-1)\n" +
                        "  (instance ?walk-3 Walking)\n" +
                        "  (time ?walk-3 ?time-2)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary went after midnight.
     * prep_after(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (greaterThan(?X,?Y)).
     */
    @Test
    public void testMaryWentAfterMidnight() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_after(go-2,midnight-4), names(Mary-1,\"Mary\"), sumo(TimePoint,midnight-4), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,midnight-4), hour(time-1,00-4), time(go-2,time-1), minute(time-1,00-4)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?go-2 ?time-1 ?midnight-4) \n" +
                        "(and \n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (greaterThan ?go-2 ?midnight-4)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (time ?go-2 ?time-1)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary went before midnight.
     * prep_before(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (lessThan(?X,?Y)).
     */
    @Test
    public void testMaryWentBeforeMidnight() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_before(go-2,midnight-4), sumo(Transportation,go-2), names(Mary-1,\"Mary\"), sumo(TimePoint,midnight-4), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,midnight-4), hour(time-1,00-4), time(go-2,time-1), minute(time-1,00-4)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?go-2 ?Mary-1 ?time-1 ?midnight-4) \n" +
                        "(and \n" +
                        "  (agent ?go-2 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (lessThan ?go-2 ?midnight-4)\n" +
                        "  (time ?go-2 ?time-1)\n" +
                        "  (instance ?go-2 Transportation)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary went along with John.
     * prep_along_with(?X,?Y), +sumo(Human,?Y) ==> (agent(?X,?Y)).
     */
    @Test
    public void testMaryWentAlongWithJohn() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_along_with(go-2,John-5), attribute(John-5,Male), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Human,John-5), names(John-5,\"John\"), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,John-5)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?go-2 ?Mary-1 ?John-5) \n" +
                        "(and \n" +
                        "  (agent ?go-2 ?John-5)\n" +
                        "  (attribute ?John-5 Male)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?John-5 \"John\")\n" +
                        "  (instance ?John-5 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary went along with the man.
     * prep_along_with(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Person) ==> (agent(?X,?Y)).
     */
    @Test
    public void testMaryWentAlongWithMan() {
        String input = "det(man-6,the-5), root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_along_with(go-2,man-6), sumo(Man,man-6), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,man-6)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?go-2 ?Mary-1 ?man-6) \n" +
                        "(and \n" +
                        "  (agent ?go-2 ?man-6)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?man-6 Man)\n" +
                        "  (instance ?Mary-1 Human))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

    /** *************************************************************
     * Mary has walked close by the house.
     * prep_close_by(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Near)}.
     */
    @Test
    public void testMaryHasWalkedCloseBy() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,have-2), det(house-7,the-6), prep_close_by(walk-3,house-7), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-3), sumo(House,house-7), number(SINGULAR,Mary-1), tense(PRESENT,walk-3), aspect(PERFECT,walk-3), number(SINGULAR,house-7)";

        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);
        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        String actual = interpreter.fromKIFClauses(kifClauses);

        Formula actualFormula = new Formula(actual);

        String expected =
                "(exists (?Mary-1 ?house-7 ?walk-3) \n" +
                        "(and \n" +
                        "  (orientation ?walk-3 ?house-7 Near)\n" +
                        "  (agent ?walk-3 ?Mary-1)\n" +
                        "  (attribute ?Mary-1 Female)\n" +
                        "  (names ?Mary-1 \"Mary\")\n" +
                        "  (instance ?Mary-1 Human)\n" +
                        "  (instance ?walk-3 Walking)\n" +
                        "  (instance ?house-7 House))\n" +
                        ")";

        //assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+", " ").trim());
        assertTrue(actualFormula.logicallyEquals(expected));
    }

}
