package com.kingmang.voltage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class VoltageVm {
    public static final Map<String, Integer> INSTRUCTIONS = Map.ofEntries(
            entry("ADD", 1),
            entry("SUB", 2),
            entry("MUL", 3),
            entry("DIV", 4),
            entry("LT", 5),
            entry("GT", 6),
            entry("LEQ", 7),
            entry("GEQ", 8),
            entry("EQ", 9),
            entry("AND", 10),
            entry("OR", 11),
            entry("NOT", 12),
            entry("JMP", 13),
            entry("JMPT", 14),
            entry("JMPF", 15),
            entry("CONST", 16),
            entry("LOAD", 17),
            entry("GLOAD", 18),
            entry("STORE", 19),
            entry("GSTORE", 20),
            entry("PRINT", 21),
            entry("POP", 22),
            entry("HALT", 23),
            entry("CALL", 24),
            entry("RET", 25),
            entry("NEG", 26),
            entry("MOD", 27)
    );
    public final static int ADD = 1;
    public final static int SUB = 2;
    public final static int MUL = 3;
    public final static int DIV = 4;
    public final static int LT = 5;
    public final static int GT = 6;
    public final static int LEQ = 7;
    public final static int GEQ = 8;
    public final static int EQ = 9;
    public final static int AND = 10;
    public final static int OR = 11;
    public final static int NOT = 12;
    public final static int JMP = 13;
    public final static int JMPT = 14;
    public final static int JMPF = 15;
    public final static int CONST = 16;
    public final static int LOAD = 17;
    public final static int GLOAD = 18;
    public final static int STORE = 19;
    public final static int GSTORE = 20;
    public final static int PRINT = 21;
    public final static int POP = 22;
    public final static int HALT = 23;
    public final static int CALL = 24;
    public final static int RET = 25;
    public final static int NEG = 26;
    public final static int MOD = 27;
    public static final Map<Integer, String> INT_TO_INSTRUCTION = INSTRUCTIONS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    private int[] programMemory, stack, globalMem;
    private int pc, sp, fp;
    private int a, b;
    private boolean done, debug = false;

    public VoltageVm(int[] program) {
        sp = -1;
        pc = 0;
        fp = 0;
        done = false;
        programMemory = program;
        stack = new int[100];
        globalMem = new int[300];
    }

    public VoltageVm(String filepath) {
        sp = -1;
        pc = 0;
        done = false;
        load(filepath);
        stack = new int[100];
        globalMem = new int[300];
    }

    public VoltageVm() {
        sp = -1;
        pc = 0;
        done = false;
        stack = new int[100];
        globalMem = new int[300];
    }

    public VoltageVm(boolean debug) {
        sp = -1;
        pc = 0;
        this.debug = debug;
        done = false;
        stack = new int[100];
        globalMem = new int[300];
    }

    public void load(String path) {
        try {
            ArrayList<Integer> tmp = new ArrayList<Integer>();
            Scanner in = new Scanner(new File(path));
            while (in.hasNextInt()) {
                tmp.add(in.nextInt());
            }
            programMemory = tmp.stream().mapToInt(Integer::intValue).toArray();
        } catch (FileNotFoundException e) {
            System.err.println(e);
            programMemory = new int[]{HALT};
        }
    }

    public void load(int[] program) {
        programMemory = program.clone();
    }

    public void run() {
        done = false;
        while (!done) {
            if (debug) System.out.print(INT_TO_INSTRUCTION.get(programMemory[pc]));
            switch (programMemory[pc]) {
                case ADD -> {
                    b = pop();
                    a = pop();
                    push(a + b);
                }
                case SUB -> {
                    b = pop();
                    a = pop();
                    push(a - b);
                }
                case MUL -> {
                    b = pop();
                    a = pop();
                    push(a * b);
                }
                case DIV -> {
                    b = pop();
                    a = pop();
                    push(a / b);
                }
                case LT -> {
                    b = pop();
                    a = pop();
                    push(a < b ? 1 : 0);
                }
                case GT -> {
                    b = pop();
                    a = pop();
                    push(a > b ? 1 : 0);
                }
                case LEQ -> {
                    b = pop();
                    a = pop();
                    push(a <= b ? 1 : 0);
                }
                case GEQ -> {
                    b = pop();
                    a = pop();
                    push(a >= b ? 1 : 0);
                }
                case EQ -> {
                    b = pop();
                    a = pop();
                    push(a == b ? 1 : 0);
                }
                case AND -> {
                    b = pop();
                    a = pop();
                    push(a == 1 && b == 1 ? 1 : 0);
                }
                case OR -> {
                    b = pop();
                    a = pop();
                    push(a == 1 || b == 1 ? 1 : 0);
                }
                case NOT -> {
                    a = pop();
                    push(a == 1 ? 0 : a == 0 ? 1 : a);
                }
                case JMP -> {
                    a = pop();
                    pc = a - 1;
                }
                case JMPT -> {
                    b = pop();
                    a = pop();
                    pc = a == 1 ? b - 1 : pc;
                }
                case JMPF -> {
                    b = pop();
                    a = pop();
                    pc = a == 0 ? b - 1 : pc;
                }
                case CONST -> {
                    pc += 1;
                    push(programMemory[pc]);
                }
                case LOAD -> {
                    a = pop();
                    b = a < 0 ? fp + a - 2 : fp + a + 1;
                    push(stack[b]);
                }
                case GLOAD -> {
                    a = pop();
                    push(globalMem[a]);
                }
                case STORE -> {
                    pc += 1;
                    a = pop();
                    stack[fp - programMemory[pc] + 1] = a;
                }
                case GSTORE -> {
                    pc += 1;
                    a = pop();
                    globalMem[programMemory[pc]] = a;
                }
                case PRINT -> System.out.println(pop());
                case POP -> pop();
                case HALT -> done = true;
                case CALL -> {

                    push(programMemory[pc + 2]);
                    push(pc + 3);
                    push(fp);

                    fp = sp;
                    sp += programMemory[pc + 3];
                    pc = programMemory[pc + 1] - 1;
                }
                case RET -> {
                    a = pop();
                    sp = fp;
                    fp = pop();
                    pc = pop();
                    b = pop();
                    sp -= b;
                    push(a);
                }
                case NEG -> {
                    a = pop();
                    push(-a);
                }
                case MOD -> {
                    b = pop();
                    a = pop();
                    push(a % b);
                }
            }

            if (debug) System.out.println(" pc" + pc + " sp" + sp + " fp" + fp + " stack"
                    + Arrays.toString(Arrays.copyOfRange(stack, 0, sp + 1)));
            pc++;
        }
    }

    private void push(int n) {
        sp++;
        stack[sp] = n;
    }

    private int pop() {
        sp--;
        return stack[sp + 1];
    }



}
