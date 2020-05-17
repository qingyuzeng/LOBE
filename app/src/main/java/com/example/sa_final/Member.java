package com.example.sa_final;

    public class Member {
        private String name;
        private String phone;
        private String money;
        private String who;
        private String Imageid;

        public Member() {
        }

        public Member(String name, String phone, String money, String who, String Imageid) {
            this.name = name;
            this.phone = phone;
            this.money = money;
            this.who = who;
            this.Imageid = Imageid;
        }

        public String getName() {
            return this.name = name;
        }

        public String getPhone() {
            return this.phone = phone;
        }

        public String getMoney() {
            return this.money = money;
        }

        public String getWho() {
            return this.who = who;
        }

        public String getImageid() {
            return Imageid;
        }

        public void setImageid(String imageid) {
            Imageid = imageid;
        }
    }
