package com.hooha.maidai.chat.model;


/**
 * 当前直播信息页面
 */
public class CurLiveInfo {
    private static int members;
    private static int admires;
    private static String title;
    private static double lat1;
    private static double long1;
    private static String address = "";
    private static String coverurl = "";

    public static int roomNum;
    private static int isStartGame;
    private static int isremind;
    private static int gameTime = 0;
    private static String activeInfo;

    private static int flowers = 0;
    private static int gratuitys = 0;
    private static int hearts = 0;
    private static int flowers2 = 0;
    private static int gratuitys2 = 0;
    private static int hearts2 = 0;

    public static String hostID;
    public static String hostName;
    public static String hostAvator;
    public static String hostID2;
    public static String hostName2;
    public static String hostAvator2;

    public static int gameStatus = -1;  //-1 未开始  0 已开始  1 已投票次数

    public static int currentRequestCount = 0;

    public static int getCurrentRequestCount() {
        return currentRequestCount;
    }

    public static int getIndexView() {
        return indexView;
    }

    public static void setIndexView(int indexView) {
        CurLiveInfo.indexView = indexView;
    }

    public static int indexView = 0;

    public static void setCurrentRequestCount(int currentRequestCount) {
        CurLiveInfo.currentRequestCount = currentRequestCount;
    }

    public static String getHostID() {
        return hostID;
    }

    public static void setHostID(String hostID) {
        CurLiveInfo.hostID = hostID;
    }

    public static String getHostName() {
        return hostName;
    }

    public static void setHostName(String hostName) {
        CurLiveInfo.hostName = hostName;
    }

    public static String getHostAvator() {
        return hostAvator;
    }

    public static void setHostAvator(String hostAvator) {
        CurLiveInfo.hostAvator = hostAvator;
    }

    public static String getHostID2() {
        return hostID2;
    }

    public static void setHostID2(String hostID2) {
        CurLiveInfo.hostID2 = hostID2;
    }

    public static String getHostName2() {
        return hostName2;
    }

    public static void setHostName2(String hostName2) {
        CurLiveInfo.hostName2 = hostName2;
    }

    public static String getHostAvator2() {
        return hostAvator2;
    }

    public static void setHostAvator2(String hostAvator2) {
        CurLiveInfo.hostAvator2 = hostAvator2;
    }

    public static int getMembers() {
        return members;
    }

    public static void setMembers(int members) {
        CurLiveInfo.members = members;
    }

    public static int getAdmires() {
        return admires;
    }

    public static void setAdmires(int admires) {
        CurLiveInfo.admires = admires;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        CurLiveInfo.title = title;
    }

    public static double getLat1() {
        return lat1;
    }

    public static void setLat1(double lat1) {
        CurLiveInfo.lat1 = lat1;
    }

    public static double getLong1() {
        return long1;
    }

    public static void setLong1(double long1) {
        CurLiveInfo.long1 = long1;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        CurLiveInfo.address = address;
    }

    public static int getRoomNum() {
        return roomNum;
    }

    public static void setRoomNum(int roomNum) {
        CurLiveInfo.roomNum = roomNum;
    }

    public static int getIsStartGame() {
        return isStartGame;
    }

    public static void setIsStartGame(int isStartGame) {
        CurLiveInfo.isStartGame = isStartGame;
    }

    public static int getIsremind() {
        return isremind;
    }

    public static void setIsremind(int isremind) {
        CurLiveInfo.isremind = isremind;
    }

    public static int getGameTime() {
        return gameTime;
    }

    public static void setGameTime(int gameTime) {
        CurLiveInfo.gameTime = gameTime;
    }

    public static String getActiveInfo() {
        return activeInfo;
    }

    public static void setActiveInfo(String activeInfo) {
        CurLiveInfo.activeInfo = activeInfo;
    }

    public static String getCoverurl() {
        return coverurl;
    }

    public static void setCoverurl(String coverurl) {
        CurLiveInfo.coverurl = coverurl;
    }

    public static String getChatRoomId() {
        return "" + roomNum;
    }

    public static int getHearts() {
        return hearts;
    }

    public static void setHearts(int hearts) {
        CurLiveInfo.hearts = hearts;
    }

    public static int getFlowers() {
        return flowers;
    }

    public static void setFlowers(int flowers) {
        CurLiveInfo.flowers = flowers;
    }

    public static int getGratuitys() {
        return gratuitys;
    }

    public static void setGratuitys(int gratuitys) {
        CurLiveInfo.gratuitys = gratuitys;
    }

    public static int getFlowers2() {
        return flowers2;
    }

    public static void setFlowers2(int flowers2) {
        CurLiveInfo.flowers2 = flowers2;
    }

    public static int getGratuitys2() {
        return gratuitys2;
    }

    public static void setGratuitys2(int gratuitys2) {
        CurLiveInfo.gratuitys2 = gratuitys2;
    }

    public static int getHearts2() {
        return hearts2;
    }

    public static void setHearts2(int hearts2) {
        CurLiveInfo.hearts2 = hearts2;
    }

    public static int getGameStatus() {
        return gameStatus;
    }

    public static void setGameStatus(int gameStatus) {
        CurLiveInfo.gameStatus = gameStatus;
    }
}
