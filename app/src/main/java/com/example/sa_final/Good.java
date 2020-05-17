package com.example.sa_final;

class Good {
    private String name;
    private String intro;
    private String money;
    private String Imageid;
    private String id;
    private String number;

    public Good() {
    }

    public Good(String name, String intro, String money, String Imageid, String id,String number) {
        this.name = name;
        this.intro = intro;
        this.money = money;
        this.Imageid = Imageid;
        this.id=id;
        this.number=number;
    }

    public String getName() {
        return this.name = name;
    }

    public String getIntro() {
        return this.intro = intro;
    }
    public String getId() {
        return this.id = id;
    }

    public String getMoney() {
        return this.money = money;
    }


    public String getImageid() {
        return Imageid;
    }
    public String getNumber() {
        return number;
    }

}
