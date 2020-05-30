class Problem {
    public static void main(String[] args) {
        String strToFind = "test";
        int findedIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(strToFind)) {
                findedIndex = i;
                break;
            }
        }
        System.out.println(findedIndex);
    }
}