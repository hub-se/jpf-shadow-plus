/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package gov.nasa.jpf.symbc;

public class FloatTest extends InvokeTest {

  // x > 1.1f

  protected static String PC1;// = "x > CONST_1.100000023841858";
  //
  // (x <= 1.1f)
  
  protected static String PC2;// = "x < CONST_1.100000023841858";
  //protected static String PC10 = "x_1_SYMREAL > CONST_1.100000023841858";
  protected static String PC3;// = "CONST_1.100000023841858 = x";
  protected static String PC11;// = "(x + y) < CONST_30.0";
  protected static String PC12;// = "y < CONST_30.0";
  //
  // [(x > 1.1f) && ((z := y) > 30.0f)] || [(x < 1.1f) && ((z := x+y) > 30.0f)] || [(x == 1.1f) && ((z := x+y) > 30.0f)]

  protected static String PC4; // = "(x + y) > CONST_30.0";
  protected static String PC5; // = "y > CONST_30.0";
  //
  // [((z := x+y) < 30.0f) && (x == 1.1f)] || [(x < 1.1f) && ((z := x+y) < 30.0f)] ||
  // [(x < 1.1f) && ((z := x+y) == 30.0f)] || [(x == 1.1f) && ((z := x+y) == 30.0f)] ||
  // [(x > 1.1f) && ((z := y) < 30.0f)] || [(x > 1.1f) && ((z := y) == 30.0f)]
  protected static String PC6; // = "CONST_30.0 = (x + y)";
  protected static String PC7; // = "(x + y) < CONST_30.0";
  protected static String PC8; // = "y < CONST_30.0";
  protected static String PC9; // = "CONST_30.0 = y";

  protected static void testFloat(float x, float y) {

    PC1 = TestPC.doublePC1("x",">",1.100000023841858);
    PC2 = TestPC.doublePC1("x","<",1.100000023841858);
    PC3 = TestPC.doublePC2(1.100000023841858,"=","x");
    PC4 = TestPC.doublePC3("x","+","y",">",30.0);
    PC5 = TestPC.doublePC1("y",">",30.0);
    PC6 = TestPC.doublePC4(30.0,"x","+","y","=");
    PC7 = TestPC.doublePC3("x","+","y","<",30.0);
    PC8 = TestPC.doublePC1("y","<",30.0);
    PC9 = TestPC.doublePC2(30.0,"=","y");

    String pc = "";
    float z = x + y;

    if (x > 1.1f) {
      assert pcMatches(PC1) : makePCAssertString("TestFloatSpecial1.testFloat1 if x > 1.1f", PC1, 
        TestUtils.getPathCondition());
      z = y;
    } else {
      assert (pcMatches(PC2) || pcMatches(PC3)) : makePCAssertString("TestFloatSpecial1.testFloat1 x <= 1.1f",
              "either\n" + PC2 + "\nor\n" + PC3, TestUtils.getPathCondition());
    }
    pc = trimPC(TestUtils.getPathCondition());
    if (z > 30.0f) {
      assert (pcMatches(joinPC(PC4, pc)) || pcMatches(joinPC(PC5, pc))) : makePCAssertString(
              "TestFloatSpecial1.testFloat1 z <= 30.0f", "one of\n" + joinPC(PC4, pc) + "\nor\n"
              + joinPC(PC5, pc), TestUtils.getPathCondition());
      z = 91.0f;
    } else {
      assert (pcMatches(joinPC(PC7, pc)) || pcMatches(joinPC(PC6, pc)) || 
        pcMatches(joinPC(PC4, pc)) || pcMatches(joinPC(PC8, pc)) ||pcMatches(joinPC(PC9, pc))||
        pcMatches(joinPC(PC5, pc))) : makePCAssertString(
              "TestFloatSpecial1.testFloat1 z <= 30.0f", "one of\n" + joinPC(PC7, pc) +
              "\nor\n" + joinPC(PC8, pc)+ "\nor\n" + joinPC(PC6, pc)+
               "\nor\n" + joinPC(PC4, pc)+" \nor\n" + joinPC(PC9, pc)
              ,TestUtils.getPathCondition());
    }
  }
}
