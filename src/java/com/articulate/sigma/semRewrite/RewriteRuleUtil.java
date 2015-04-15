package com.articulate.sigma.semRewrite;
/*
Copyright 2014-2015 IPsoft

Author: Peigen You Peigen.You@ipsoft.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program ; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307 USA
*/

import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import com.google.common.base.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.json.JsonObject;
import java.io.*;
import java.util.*;

import static com.articulate.sigma.semRewrite.Interpreter.canon;

public final class RewriteRuleUtil extends RuleSet {

    static String fileLocation = "/Users/peigenyou/workspace/sigma/KBs/WordNetMappings/SemRewrite.txt";

    private RewriteRuleUtil() {

    }

    /**
     * **********************************************************
     * update a Ruleset's indexed rule's CNF
     */
    public static void updateCNF(int index, CNF f, RuleSet rs) {

        Rule r = rs.rules.get(index);
        r.cnf = f;
        System.out.println(r);
        Rule newr = Rule.parse(new Lexer(r.toString()));
        System.out.println(newr);
        rs.rules.set(index, newr);
    }

    /**
     * **********************************************************
     */
    private static void updateCNFTest(RuleSet rs) {

        rs = new RuleSet().parse(new Lexer("prep_about(?X,?Y) ==> {refers(?X,?Y)}.\n" + "prep_about(?H,?Y) ==> {refers(?H,?Y)}.\n"));
        rs = Clausifier.clausify(rs);
        System.out.println(rs);
        for (int i = 0; i < rs.rules.size(); ++i) {
            Rule r = rs.rules.get(i);
            CNF unifier = r.cnf;
            for (int j = i + 1; j < rs.rules.size(); ++j) {

                CNF unified = rs.rules.get(j).cnf;

                System.out.println("unified = " + unified + "   " + unifier);
                HashMap<String, String> map = unifier.unify(unified);
                if (map == null || map.size() < 1) {
                    continue;
                }
                System.out.printf("Unification found between index %d and %d map = %s \n", i, j, map);
                System.out.println("Before unification---------" + unified + "   " + unifier + "\n------------");
                unified = unified.applyBindings(map);
                System.out.println("After unification---------" + unified + "   " + unifier + "\n-----------");
                boolean m = SemRewriteRuleCheck.isCNFSubsumed(unified, unifier);
                System.out.println("After update---------" + m + "\n-----------");

            }
        }

    }

    /**
     * **********************************************************
     * load RuleSet from "SemRewrite.txt"
     */
    public static RuleSet loadRuleSet() {

        RuleSet rs;
        KBmanager kb = KBmanager.getMgr();
        kb.initializeOnce();

        String f = KBmanager.getMgr().getPref("kbDir") + File.separator + "WordNetMappings" + File.separator + "SemRewrite.txt";
        String pref = KBmanager.getMgr().getPref("SemRewrite");
        if (!Strings.isNullOrEmpty(pref))
            f = pref;
        if (f.indexOf(File.separator.toString(), 2) < 0)
            f = "/home/apease/SourceForge/KBs/WordNetMappings" + f;
        try {
            RuleSet rsin = RuleSet.readFile(f);
            rs = canon(rsin);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        rs = Clausifier.clausify(rs);
        return rs;
    }



    /**
     * **********************************************************
     */
    public static void main(String[] args) {

        ArrayList<Integer> getSubsumed = new ArrayList<Integer>();
        ArrayList<Integer> subsumer = new ArrayList<Integer>();
        RuleSet rs = loadRuleSet();
        String input = "";
        System.out.println("SemRewrite.txt loaded. There are " + rs.rules.size() + " rules.");
 //       SemRewriteRuleCheck.checkRuleSet(rs);
        System.out.println("1.Will check rules entered by default. Please enter rule.\nThere are other functions:\nreload  will reload the Semrewrite.txt and check the ruleset\n!filePath   no spaces will load the file with sentences and find one common CNF.\nexit/quit to quit");
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                System.out.print("\nEnter :");
                input = scanner.nextLine().trim();
                if (!Strings.isNullOrEmpty(input) &&  !input.equals("exit") && !input.equals("quit")) {
                    if(input.equals("reload")){
                        rs=loadRuleSet();
                        SemRewriteRuleCheck.checkRuleSet(rs);
                        continue;
                    }
                    if(input.startsWith("!")){
                        String path=input.substring(1);
                        String[] strs=CommonCNFUtil.loadSentencesFromTxt(path);
                        CNF cnf=CommonCNFUtil.findOneCommonCNF(CommonCNFUtil.generateCNFForStringSet(strs).values());
                        System.out.println("\nThe common CNF is :"+cnf);
                        continue;
                    }
                    Rule r = Rule.parseString(input);
                    System.out.println("The rule entered is :: " + r + "\n");
                    SemRewriteRuleCheck.isRuleSubsumedByRuleSet(r, rs, getSubsumed, subsumer);

                    System.out.println("Following " + getSubsumed.size() + " rules would subsume the rule entered: \n");
                    for (int k : getSubsumed) {
                        System.out.println("Line Number:" + rs.rules.get(k).startLine + " : " + rs.rules.get(k));
                    }
                    System.out.println("---------------------------------------------------------------");
                    System.out.println("Following " + subsumer.size() + " rules would be subsumed by the rule entered: \n");
                    for (int k : subsumer) {
                        System.out.println("Line Number:" + rs.rules.get(k).startLine + " : " + rs.rules.get(k));
                    }
                    System.out.println("\n");
                }
            }
            catch (Throwable e) {
                continue;
            }

        } while (!input.equals("exit") && !input.equals("quit"));
    }
//    public static void main(String[] args) {

//        String[] strings = new String[]{"Amelia flies.", "John walks."};
//        String [] strings=loadSentencesFormJsonFile("test/corpus/java/resources/IRtests.json");
//        Map<Integer, CNF> res = generateCNFForStringSet(strings);
//        String path=saveCNFMaptoFile(res, strings, "test.json");
//        ArrayList<String> strs=new ArrayList<String>();
//        Map<Integer, CNF> res=new HashMap<Integer,CNF>();
//        loadCNFMapfromFile("test.json", res,strs);
//        String []strings=strs.toArray(new String[strs.size()]);
//        for (Map.Entry e : res.entrySet()) {
//            System.out.println(e);
//        }
//        Map<Integer, Map<Integer, CNF>> rr = getCommonCNF(res);
//        for (Map.Entry e : rr.entrySet()) {
//            System.out.println(e);
//        }
//        Map<CNF, Set<Pair<Integer, Integer>>> re = reverseMap(rr);
//        for(CNF cnf:re.keySet()){
//            System.out.println(cnf.toString()+re.get(cnf));
//        }
//    }

}
