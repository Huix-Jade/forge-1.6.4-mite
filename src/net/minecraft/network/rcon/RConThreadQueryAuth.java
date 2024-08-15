package net.minecraft.network.rcon;

import java.net.DatagramPacket;
import java.util.Date;
import java.util.Random;

class RConThreadQueryAuth {
   private long timestamp;
   private int randomChallenge;
   private byte[] requestId;
   private byte[] challengeValue;
   private String requestIdAsString;
   // $FF: synthetic field
   final RConThreadQuery queryThread;

   public RConThreadQueryAuth(RConThreadQuery var1, DatagramPacket var2) {
      this.queryThread = var1;
      this.timestamp = (new Date()).getTime();
      byte[] var3 = var2.getData();
      this.requestId = new byte[4];
      this.requestId[0] = var3[3];
      this.requestId[1] = var3[4];
      this.requestId[2] = var3[5];
      this.requestId[3] = var3[6];
      this.requestIdAsString = new String(this.requestId);
      this.randomChallenge = (new Random()).nextInt(16777216);
      this.challengeValue = String.format("\t%s%d\u0000", this.requestIdAsString, this.randomChallenge).getBytes();
   }

   public Boolean hasExpired(long var1) {
      return this.timestamp < var1;
   }

   public int getRandomChallenge() {
      return this.randomChallenge;
   }

   public byte[] getChallengeValue() {
      return this.challengeValue;
   }

   public byte[] getRequestId() {
      return this.requestId;
   }
}
