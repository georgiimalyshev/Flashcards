import java.lang.reflect.Array;
import java.util.Collections;

/* Please, do not rename it */
class Problem {

    public static void main(String[] args) {
        String operator = args[0];
        switch (operator) {
            case "MAX" : {
                int max = Integer.MIN_VALUE;
                for (int i = 1; i < args.length; i++) {
                    int curEl = Integer.parseInt(args[i]);
                    max = Math.max(max, curEl);
                }
                System.out.println(max);
                break;
            }
            case "MIN" : {
                int min = Integer.MAX_VALUE;
                for (int i = 1; i < args.length; i++) {
                    int curEl = Integer.parseInt(args[i]);
                    min = Math.min(min, curEl);
                }
                System.out.println(min);
                break;
            }
            case "SUM" : {
                int sum = 0;
                for (int i = 1; i < args.length; i++) {
                    int curEl = Integer.parseInt(args[i]);
                    sum += curEl;
                }
                System.out.println(sum);
                break;
            }
            default: {
                break;
            }
        }
    }
}