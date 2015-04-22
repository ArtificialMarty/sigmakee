package com.articulate.sigma.semRewrite;

import com.articulate.sigma.UnitTestBase;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Tests the SemRewrite of a given parse.
 * In: Dependency parse as string.
 * Out: The right-hand-side of a SemRewrite rule that is triggered by the input.
 */
public class SemRewriteTest extends UnitTestBase {

    private static Interpreter interpreter;

    @Before
    public void setUpInterpreter()  {
        interpreter = new Interpreter();
        interpreter.inference = false;
        interpreter.initialize();
    }

    private void doTest(String input, String[] expectedOutput) {
        ArrayList<CNF> cnfInput = interpreter.getCNFInput(input);

        ArrayList<String> kifClauses = interpreter.interpretCNF(cnfInput);
        Set<String> actual = Sets.newHashSet(kifClauses);
        Set<String> cleanedActual = actual.stream().map(str -> str.replaceAll("\\s+", " ")).collect(Collectors.toSet());

        assertThat(cleanedActual, hasItems(expectedOutput));
    }

    /****************************************************************
     * Mary has walked close to the house.
     * prep_close_to(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Near)}.
     */
    @Test
    public void testMaryHasWalkedCloseTo() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,have-2), det(house-7,the-6), prep_close_to(walk-3,house-7), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-3), sumo(House,house-7), number(SINGULAR,Mary-1), tense(PRESENT,walk-3), aspect(PERFECT,walk-3), number(SINGULAR,house-7)";

        String[] expected = {
                "(orientation walk-3 house-7 Near)"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary walked next to the house.
     * prep_next_to(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y Near)}.
     */
    @Test
    public void testMaryWalkedNextTo() {
        String input = "root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), det(house-6,the-5), prep_next_to(walk-2,house-6), sumo(House,house-6), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Walking,walk-2), number(SINGULAR,Mary-1), tense(PAST,walk-2), number(SINGULAR,house-6)";

        String[] expected = {
                "(orientation walk-2 house-6 Near)"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary is over the house.
     * prep_over(?V,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?LOC ?Y Above) (located ?V ?LOC)}.
     */
    @Test
    public void testMaryIsOverHouse() {
        String input = "root(ROOT-0,be-2), nsubj(be-2,Mary-1), det(house-5,the-4), prep_over(be-2,house-5), sumo(House,house-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PRESENT,be-2), number(SINGULAR,house-5)";

        String[] expected = {
                "(orientation ?LOC house-5 Above) (located be-2 ?LOC)"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary went prior to eating.
     * prep_prior_to(?X,?Y), +sumo(?C,?Y), isSubclass(?C,Process) ==> {(earlier ?X (WhenFn ?Y))}.
     */
    @Test
    public void testMaryGoPriorToEating() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_prior_to(go-2,eating-5), sumo(Transportation,go-2), names(Mary-1,\"Mary\"), sumo(Eating,eating-5), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,eating-5)";

        String[] expected = {
                "(earlier go-2 (WhenFn eating-5))"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary went prior to Christmas Day.
     * prep_prior_to(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> {(earlier ?X ?Y)}.
     */
    @Test
    public void testMaryGoPriorToChristmas() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_prior_to(go-2,ChristmasDay-5), sumo(Transportation,go-2), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Day,ChristmasDay-5), number(SINGULAR,Mary-1), tense(PAST,go-2), number(SINGULAR,Christmas-5), number(SINGULAR,Day-6)";

        String[] expected = {
                "(earlier go-2 ChristmasDay-5)"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary walks in front of the house.
     * prep_in_front_of(?V,?Y), nsubj(?V,?Z) ==> {(and (equal ?F (FrontFn ?Y)) (orientation ?Z ?F Near))}.
     */
    @Test
    public void testMaryWalksInFrontOf() {
        String input = "det(house-7,the-6), root(ROOT-0,walk-2), nsubj(walk-2,Mary-1), prep_in_front_of(walk-2,house-7), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(House,house-7), number(SINGULAR,Mary-1), tense(PRESENT,walk-2), number(SINGULAR,front-4), number(SINGULAR,house-7)";

        String[] expected = {
                "(and (equal ?F (FrontFn house-7)) (orientation Mary-1 ?F Near))"
        };

        doTest(input, expected);
    }

    /****************************************************************
     * Mary is on top of the house.
     * prep_on_top_of(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Object) ==> {(orientation ?X ?Y On)}.
     */
    @Test
    public void testMaryIsOnTopOf() {
        String input = "det(house-7,the-6), root(ROOT-0,be-2), nsubj(be-2,Mary-1), prep_on_top_of(be-2,house-7), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(House,house-7), number(SINGULAR,Mary-1), tense(PRESENT,be-2), number(SINGULAR,top-4), number(SINGULAR,house-7)";

        String[] expected = {
                "(orientation be-2 house-7 On)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary has walked since Christmas Day.
     * prep_since(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (starts(?Y,?X)).
     */
    @Test
    public void testMaryWalkSinceChristmasDay() {
        String input = "root(ROOT-0,walk-3), nsubj(walk-3,Mary-1), aux(walk-3,have-2), prep_since(walk-3,ChristmasDay-5), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Day,ChristmasDay-5), sumo(Walking,walk-3), number(SINGULAR,Mary-1), tense(PRESENT,walk-3), aspect(PERFECT,walk-3), number(SINGULAR,Christmas-5), number(SINGULAR,Day-6)";

        String[] expected = {
                "(starts ChristmasDay-5 walk-3)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary goes on Christmas Day.
     * prep_on(?X,?Y), +sumo(?C,?Y), isCELTclass(?C,Time) ==> (time(?X,?Y)).
     */
    @Test
    public void testMaryGoOnChristmasDay() {
        String input = "root(ROOT-0,go-2), nsubj(go-2,Mary-1), prep_on(go-2,ChristmasDay-4), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), sumo(Day,ChristmasDay-4), number(SINGULAR,Mary-1), tense(PRESENT,go-2), number(SINGULAR,Christmas-4), number(SINGULAR,Day-5)";

        String[] expected = {
                "(time go-2 ChristmasDay-4)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary is indoors.
     * advmod(?V,?Y), nsubj(?V,?P), +sumo(Indoors,?Y) ==>
     *         {(located ?P ?INDOORS) (instance ?INDOORS Indoors) (exists (?BUILDING) (and (instance ?BUILDING Building) (orientation ?INDOORS ?BUILDING Inside)))}.
     */
    @Test
    public void testMaryIsIndoors() {
        String input = "root(ROOT-0,be-2), nsubj(be-2,Mary-1), advmod(be-2,indoors-3), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Indoors,indoors-3), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PRESENT,be-2)";

        String[] expected = {
                "(located Mary-1 ?INDOORS) (instance ?INDOORS Indoors) (exists (?BUILDING) (and (instance ?BUILDING Building) (orientation ?INDOORS ?BUILDING Inside)))"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary is outdoors.
     * advmod(?V,?Y), nsubj(?V,?P), +sumo(Outdoors,?Y) ==>
     *       {(located ?P ?OUTDOORS) (instance ?OUTDOORS Outdoors) (not (exists (?BUILDING) (and (instance ?BUILDING Building) (orientation ?OUTDOORS ?BUILDING Inside))))}.
     */
    @Test
    public void testMaryIsOutdoors() {
        String input = "root(ROOT-0,be-2), nsubj(be-2,Mary-1), advmod(be-2,outdoors-3), names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Outdoors,outdoors-3), sumo(Human,Mary-1), number(SINGULAR,Mary-1), tense(PRESENT,be-2)";

        String[] expected = {
                "(located Mary-1 ?OUTDOORS) (instance ?OUTDOORS Outdoors) (not (exists (?BUILDING) (and (instance ?BUILDING Building) (orientation ?OUTDOORS ?BUILDING Inside))))"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary has made a house.
     * aux(?V,have*), tense(PRESENT,?V), aspect(PERFECT,?V) ==> (past(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryHasMadeAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-3), nsubj(make-3,Mary-1), tense(PRESENT,make-3), aspect(PERFECT,make-3), aux(make-3,have-2), sumo(House,house-5), number(SINGULAR,house-5), dobj(make-3,house-5), det(house-5,a-4)";

        String[] expected = {
                "(patient make-3 house-5)",
                "(earlier (WhenFn make-3) Now)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary has been making a house.
     * aux(?V,have*), aux(?V,be*), tense(PRESENT,?V), aspect(PROGRESSIVEPERFECT,?V) ==> (past(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryHasBeenMakingAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-4), nsubj(make-4,Mary-1), sumo(IntentionalProcess,make-4), tense(PRESENT,make-4), aspect(PROGRESSIVEPERFECT,make-4), aux(make-4,have-2), aux(make-4,be-3), sumo(House,house-6), number(SINGULAR,house-6), dobj(make-4,house-6), det(house-6,a-5)";

        String[] expected = {
                "(patient make-4 house-6)",
                "(earlier (WhenFn make-4) Now)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary made a house.
     * tense(PAST,?V) ==> (past(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryMadeAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-2), nsubj(make-2,Mary-1), tense(PAST,make-2), sumo(House,house-4), number(SINGULAR,house-4), dobj(make-2,house-4), det(house-4,a-3)";

        String[] expected = {
                "(patient make-2 house-4)",
                "(earlier (WhenFn make-2) Now)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary makes a house.
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryMakesAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-2), nsubj(make-2,Mary-1), sumo(IntentionalProcess,make-2), tense(PRESENT,make-2), sumo(House,house-4), number(SINGULAR,house-4), dobj(make-2,house-4), det(house-4,a-3)";

        String[] expected = {
                "(patient make-2 house-4)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary will make a house.
     * aux(?V,will*), tense(FUTURE,?V) ==> (future(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryWillMakeAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-3), nsubj(make-3,Mary-1), sumo(IntentionalProcess,make-3), tense(FUTURE,make-3), aux(make-3,will-2), sumo(House,house-5), number(SINGULAR,house-5), dobj(make-3,house-5), det(house-5,a-4)";

        String[] expected = {
                "(patient make-3 house-5)",
                "(earlier Now (WhenFn make-3))"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary will have made a house.
     * aux(?V,will*), aux(?V,have-3), tense(FUTURE,?V), aspect(PERFECT,?V) ==> (future(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryWillHaveMadeAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-4), nsubj(make-4,Mary-1), tense(FUTURE,make-4), aspect(PERFECT,make-4), aux(make-4,will-2), aux(make-4,have-3), sumo(House,house-6), number(SINGULAR,house-6), dobj(make-4,house-6), det(house-6,a-5)";

        String[] expected = {
                "(patient make-4 house-6)",
                "(earlier Now (WhenFn make-4))"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary will have been making a house.
     * aux(?V,will*), aux(?V,have-3), aux(?V,be*), tense(FUTURE,?V), aspect(PROGRESSIVEPERFECT,?V) ==> (future(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryWillHaveBeenMakingAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-5), nsubj(make-5,Mary-1), sumo(IntentionalProcess,make-5), tense(FUTURE,make-5), aspect(PROGRESSIVEPERFECT,make-5), aux(make-5,will-2), aux(make-5,have-3), aux(make-5,be-4), sumo(House,house-7), number(SINGULAR,house-7), dobj(make-5,house-7), det(house-7,a-6)";

        String[] expected = {
                "(patient make-5 house-7)",
                "(earlier Now (WhenFn make-5))"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary had made a house.
     * aux(?V,have*), tense(PAST,?V), aspect(PERFECT,?V) ==> (past(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryHadMadeAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-3), nsubj(make-3,Mary-1), tense(PAST,make-3), aspect(PERFECT,make-3), aux(make-3,have-2), sumo(House,house-5), number(SINGULAR,house-5), dobj(make-3,house-5), det(house-5,a-4)";

        String[] expected = {
                "(patient make-3 house-5)",
                "(earlier (WhenFn make-3) Now)"
        };

        doTest(input, expected);
    }

    /** *************************************************************
     * Mary had been making a house.
     * aux(?V,have*), aux(?V,be*), tense(PAST,?V), aspect(PROGRESSIVEPERFECT,?V) ==> (past(?V,?DUMMY)).
     * patient(?X,?Y) ==> {(patient ?X ?Y)}.
     */
    @Test
    public void testMaryHadBeenMakingAHouse() {
        String input = "names(Mary-1,\"Mary\"), attribute(Mary-1,Female), sumo(Human,Mary-1), number(SINGULAR,Mary-1), root(ROOT-0,make-4), nsubj(make-4,Mary-1), sumo(IntentionalProcess,make-4), tense(PAST,make-4), aspect(PROGRESSIVEPERFECT,make-4), aux(make-4,have-2), aux(make-4,be-3), sumo(House,house-6), number(SINGULAR,house-6), dobj(make-4,house-6), det(house-6,a-5)";

        String[] expected = {
                "(patient make-4 house-6)",
                "(earlier (WhenFn make-4) Now)"
        };

        doTest(input, expected);
    }

}
