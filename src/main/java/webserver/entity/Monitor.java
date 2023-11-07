package webserver.entity;

import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Monitor {
    private List<Mote> motes = new LinkedList<>();
    private List<Gateway> gateways = new LinkedList<>();
}
