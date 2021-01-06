package smartdice.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 *
 * @author Marcin Koziel
 */
public class ProfileContainer {

    private final int STRING_LENGTH = 15;
    private final int RECORD_LENGTH = 150;

    private final ArrayList<PlayerProfile> playerProfiles;

    private static final ProfileContainer profileContainer = new ProfileContainer();

    private ProfileContainer() {
//        playerProfiles =  new ArrayList<>();
        playerProfiles = readPlayerContainer();
    }

    public static ProfileContainer getInstance(){
        return profileContainer;
    }

    public PlayerProfile createPlayerProfile(String username) {
        PlayerProfile newPlayerProfile = new PlayerProfile(username);
        if (isPlayerProfile(username) == false) {
            playerProfiles.add(newPlayerProfile);
            return newPlayerProfile;
        }
        return null;
    }

    public Boolean isPlayerProfile(String username) {
        for (PlayerProfile playerProfile : playerProfiles) {
            if (playerProfile.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public PlayerProfile getPlayerProfile(String username, String password) {
        for (PlayerProfile playerProfile : playerProfiles) {
            if (playerProfile.getUsername().equals(username)) {
                if (playerProfile.getPassword().equals(password))
                return playerProfile;
            }
        }
        return null;
    }

    private String stringBuffer(String string, int size) {
        if (string.length() < size) {
            int numSpaces = size - string.length();
            StringBuilder stringBuilder = new StringBuilder(string);
            for (int i = 0; i < numSpaces; i++) {
                stringBuilder.append(" ");
            }
            string = stringBuilder.toString();
        } else {
            string = string.substring(0, size);
        }

        return string;
    }

    private String readRandomAccessString(RandomAccessFile raf) {

        StringBuilder rafString = new StringBuilder();
        try {
            for (int i = 0; i < STRING_LENGTH; i++) {
                rafString.append(raf.readChar());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rafString.toString();
    }

    private void writePlayerProfile(PlayerProfile playerProfile, RandomAccessFile raf) throws IOException {

        raf.writeDouble(playerProfile.getBalance());
        raf.writeChars(stringBuffer(playerProfile.getName().trim(), STRING_LENGTH));
        raf.writeChars(stringBuffer(playerProfile.getUsername().trim(), STRING_LENGTH));
        raf.writeChars(stringBuffer(playerProfile.getPassword().trim(), STRING_LENGTH));
        raf.writeChars(stringBuffer(playerProfile.getEmail().trim(), STRING_LENGTH));

    }

    private PlayerProfile readPlayerProfile(RandomAccessFile raf) throws IOException {
        PlayerProfile loadedProfile = new PlayerProfile();
        int profileId;

            profileId = raf.readInt();
            loadedProfile.setBalance(raf.readDouble());
            loadedProfile.setName(readRandomAccessString(raf).trim());
            loadedProfile.setUsername(readRandomAccessString(raf).trim());
            loadedProfile.setPassword(readRandomAccessString(raf).trim());
            loadedProfile.setEmail(readRandomAccessString(raf).trim());

        return loadedProfile;
    }

    public void writeProfileContainer() {

        try {
            File file = new File("test.data");
            if (file.exists()){
                file.delete();
            }
            RandomAccessFile raf = new RandomAccessFile("test.data", "rw");

            for (int i = 0; i < playerProfiles.size(); i++) {

                raf.seek(i*RECORD_LENGTH);
                raf.writeInt(i); // Writing ProfileID
                writePlayerProfile(playerProfiles.get(i), raf);
            }
            raf.seek(playerProfiles.size()*RECORD_LENGTH);
            raf.writeInt(0);

            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PlayerProfile> readPlayerContainer() {

        ArrayList<PlayerProfile> loadedProfileContainer = new ArrayList<>();
        try{
            RandomAccessFile raf = new RandomAccessFile("test.data", "r");

                long rafLength = (raf.length()/RECORD_LENGTH);
                for (int i = 0; i < rafLength; i++) {
                    raf.seek(i*RECORD_LENGTH);
                    loadedProfileContainer.add(readPlayerProfile(raf));
                }

        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        return loadedProfileContainer;

    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

        for (PlayerProfile playerProfile : playerProfiles){
            str.append(playerProfile.toString()).append("\n");
        }
        return str.toString();
    }
}