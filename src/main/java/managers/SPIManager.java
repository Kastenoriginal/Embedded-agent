package managers;

import core.Networking;
import io.silverspoon.bulldog.core.io.bus.spi.SpiConnection;
import io.silverspoon.bulldog.core.platform.Board;
import strings.Information;
import strings.Response;

import java.io.IOException;

public class SPIManager {

    private static final int RECEIVE_LENGTH = 40;
    private static final String ADDRESS_PREFIX = "0x";

    private SpiConnection connection;

    public SPIManager(Board board, String address) {
        connection = board.getSpiBuses().get(0).createSpiConnection(Byte.decode(address));
    }

    public void sendSpiMessage(String message) {
        byte[] requestBuffer = new byte[message.length() / 2];
        for (int i = 0; i < requestBuffer.length; i++) {
            String value = ADDRESS_PREFIX + message.substring(2 * i, 2 * (i + 1));
            requestBuffer[i] = Integer.decode(value).byteValue();
            System.out.println(Information.ACCEPTING_BYTE + value);
        }
        try {
            connection.writeBytes(requestBuffer);
            System.out.println(Networking.SPI + Information.BUS_MESSAGE_WROTE_SUCCESSFULLY);
        } catch (IOException e) {
            System.out.println(e + Networking.SPI + Information.BUS_NOT_SUPPORTED_OR_DISABLED);
        } catch (NullPointerException e) {
            System.out.println(e + Information.CANNOT_WRITE_MESSAGE_ON_BUS);
        }
    }

    public String receiveSpiMessage() {
        try {
            byte[] responseBuffer = new byte[RECEIVE_LENGTH];
            int count = connection.readBytes(responseBuffer);
            StringBuilder response = new StringBuilder();
            for (int i = 0; i < count && i < RECEIVE_LENGTH; i++) {
                response.append(Integer.toHexString(responseBuffer[i]));
            }
            if (response.toString().length() > 0) {
                return response.toString();
            }
        } catch (IOException e) {
            System.out.println(e + Networking.SPI + Information.BUS_NOT_SUPPORTED_OR_DISABLED);
            return Response.INVALID_RESPONSE;
        }
        return Response.INVALID_RESPONSE;
    }
}
