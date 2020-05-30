class Application {

    String name;

    void run(String[] args) {

        System.out.println(name);
        for (String elem:
                args) {
            System.out.println(elem);

        }
    }
}