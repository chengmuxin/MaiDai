package com.hooha.maidai.chat.model;

/**
 * Gson 辅助类
 */
public class LiveInfoJson {
    private String createTime; //创建时间
    private String title;  //直播标题
    private int admireCount;  //累计点赞数
    private String cover;  //封面
    private int totalGratuity;  //累计打赏数
    private String chatRoomId;  //房间ID
    private HOST host;
    private int timeSpan;  //直播时长
    private int avRoomId;  //av房间ID
    private int watchCount;  //观看人数
    private int totalFlower;  //累计鲜花数
    private LBS lbs;

    public String getCreateTime() {
        return createTime;
    }

    public String getTitle() {
        return title;
    }

    public int getAdmireCount() {
        return admireCount;
    }

    public String getCover() {
        return cover;
    }

    public int getTotalGratuity() {
        return totalGratuity;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public HOST getHost() {
        return host;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public int getAvRoomId() {
        return avRoomId;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getTotalFlower() {
        return totalFlower;
    }

    public void setAvRoomId(int avRoomId) {
        this.avRoomId = avRoomId;
    }

    public LBS getLbs() {
        return lbs;
    }


    @Override
    public String toString() {
        return "LiveInfoJson{" +
                "createTime=" + createTime +
                ", title='" + title + '\'' +
                ", admireCount=" + admireCount +
                ", cover='" + cover + '\'' +
                ", totalGratuity=" + totalGratuity +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", host=" + host +
                ", timeSpan=" + timeSpan +
                ", avRoomId='" + avRoomId + '\'' +
                ", watchCount=" + watchCount +
                ", totalFlower=" + totalFlower +
                '}';
    }

    public class LBS {
        private double longitude;
        private double latitue;
        private String address;

        public double getLongitude() {
            return longitude;
        }

        public double getLatitue() {
            return latitue;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "LBS{" +
                    "longitude=" + longitude +
                    ", latitue=" + latitue +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public class HOST {
        private String uid;
        private String avatar;
        private String username;


        public String getUid() {
            return uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "HOST{" +
                    "uid='" + uid + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

}
