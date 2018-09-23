package com.donriver.rogers.eon.integration;

import com.donriver.data.rogers.common.valueobject.CardAction;
import com.donriver.rogers.eon.model.*;
import com.donriver.rogers.eon.operations.CardOperations;
import com.donriver.rogers.eon.operations.DeviceOperations;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Copyright DonRiver Inc. All Rights Reserved.
 *
 * @author anton.sobolev
 * @version 1.0, 07.12.2017
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CardIntegrationTest extends AbstractIntegrationTest {

    private static final String DEVICE_NAME = "LFNCARDTEST6";

    private DeviceOperations deviceOperations;
    private CardOperations cardOperations;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.deviceOperations = new DeviceOperations(this);
        this.cardOperations = new CardOperations(this);
    }

    @Test
    public void checkLoadingCardTree() throws Exception {
        final DeviceDTO device = getDevice();
        final List<CardTreeNode> nodes = cardOperations.getCardTree(device.getObjectId());

        assertThat(nodes, not(empty()));
    }

    /* the test contains a check on the size of the collection, this is necessary to check the creating/loading mechanism for protecting the card */
    @Test
    public void checkCreateAndDeleteCard() throws Exception {
        final DeviceDTO device = getDevice();
        List<CardTreeNode> nodes = cardOperations.getCardTree(device.getObjectId());
        //nodes.get(0) - ports on device - ignore
        //nodes.get(1) - contains shelves for NEXUS devices
        //nodes.get(2) - contains shelves for NEXUS devices

        //1. Create Card 1
        final CardTreeNode shelfTreeNode1 = nodes.get(1);
        final CardTreeNode slotTreeNode1 = shelfTreeNode1.getChildren().get(0);
        List<CardType> cardTypes = cardOperations.getCardTypes(slotTreeNode1.getObjectId(), true);
        slotTreeNode1.setSelectedCardType(cardTypes.get(0));//select first available type
        slotTreeNode1.setAction(CardAction.CREATE_CARD);//required

        final String cardId1 = createOrUpdateCard(device.getObjectId(), slotTreeNode1);

        //2. Create Card 2
        CardTreeNode shelfTreeNode2 = nodes.get(2);
        CardTreeNode slotTreeNode2 = shelfTreeNode2.getChildren().get(0);
        cardTypes = cardOperations.getCardTypes(slotTreeNode2.getObjectId(), true);
        slotTreeNode2.setSelectedCardType(cardTypes.get(0));//select first available type
        slotTreeNode2.setAction(CardAction.CREATE_CARD);//required

        createOrUpdateCard(device.getObjectId(), slotTreeNode2);

        //3. Modify Card - add protection
        nodes = cardOperations.getCardTree(device.getObjectId());

        final IdentifiableObject protectingCard = new IdentifiableObject();
        protectingCard.setObjectId(cardId1);
        protectingCard.setName("Test protection " + cardId1);

        shelfTreeNode2 = nodes.get(2);
        slotTreeNode2 = shelfTreeNode2.getChildren().get(0);
        slotTreeNode2.setProtectingCard(protectingCard);
        slotTreeNode2.setAction(CardAction.MODIFY_CARD);//required
        createOrUpdateCard(device.getObjectId(), slotTreeNode2);

        //4. Assert
        nodes = cardOperations.getCardTree(device.getObjectId());

        assertThat(nodes, hasSize(3));
        assertThat(nodes.get(2).getChildren().get(0).getObjectClassId(), equalTo(InventoryType.CARD.getObjectClassId()));
        assertThat(nodes.get(2).getChildren().get(0).getProtectingCard(), notNullValue());
        assertThat(nodes.get(2).getChildren().get(0).getProtectingCard().getObjectId(), equalTo(cardId1));

    }

    @Test
    public void getSFPTypes() throws Exception {
        final String slotId = "1095186";
        final MvcResult result = mockMvc.perform(
                get("/eon/cards/sfp_types/{slotId}", slotId)
                        .requestAttr("sessionContext", sessionContext))
                .andExpect(status().isOk())
                .andReturn();

        final String content = result.getResponse().getContentAsString();
        List<IdentifiableObject> types = this.mapper.readValue(content, ArrayList.class);
        ArrayList<IdentifiableObject> emptyList = new ArrayList<>();
        Assert.assertNotEquals(types, emptyList);
    }


    @Test
    public void getSFPTypesInvalidId() throws Exception {
        final String slotId = "1";
        final MvcResult result = mockMvc.perform(
                get("/eon/cards/sfp_types/{slotId}", slotId)
                        .requestAttr("sessionContext", sessionContext))
                .andExpect(status().isOk())
                .andReturn();

        final String content = result.getResponse().getContentAsString();
        List<IdentifiableObject> types = this.mapper.readValue(content, ArrayList.class);
        ArrayList<IdentifiableObject> emptyList = new ArrayList<>();
        Assert.assertEquals(types, emptyList);
    }

    @Test
    public void validateDeleteCard() throws Exception {
        final CardTreeNode cardTreeNode = new CardTreeNode();
        mockMvc.perform(
                post("/eon/cards/validate")
                        .requestAttr("sessionContext", sessionContext)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardTreeNode)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void validateDeleteUsedCard() throws Exception {
        final String deviceId = "15782557";
        final MvcResult result = mockMvc.perform(
                get("/eon/cards/tree/{deviceId}", deviceId)
                        .requestAttr("sessionContext", sessionContext))
                .andExpect(status().isOk())
                .andReturn();

        final String content = result.getResponse().getContentAsString();
        List<CardTreeNode> cardTreeNodes = this.mapper.readValue(content, List.class);
        final CardTreeNode cardTreeNode = this.mapper.readValue(mapper.writeValueAsString(cardTreeNodes.get(1)), CardTreeNode.class).getChildren().get(0);

        mockMvc.perform(
                post("/eon/cards/validate")
                        .requestAttr("sessionContext", sessionContext)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardTreeNode)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private String createOrUpdateCard(String deviceId, CardTreeNode node) throws Exception {
        final Map<String, String> result = cardOperations.createOrUpdateCard(deviceId, node);

        assertThat(result.isEmpty(), is(false));
        assertThat(result.containsKey(node.getAction().name()), is(true));

        return result.get(node.getAction().name());
    }

    private DeviceDTO getDevice() throws Exception {
        final List<DeviceDTO> devices = deviceOperations.getDevicesByName(DEVICE_NAME);
        if (CollectionUtils.isEmpty(devices)) {
            final DeviceDTO dto = createTestDevice();
            return deviceOperations.createDevice(dto);
        } else {
            return devices.get(0);
        }
    }

    private DeviceDTO createTestDevice() {
        final LocationDTO locationDto = new LocationDTO();
        locationDto.setObjectId("5750362");
        final DeviceDTO deviceDto = new DeviceDTO();
        deviceDto.setLocation(locationDto);
        deviceDto.setStatusId("150021001");
        deviceDto.setTypeId("1910000001");

        final DeviceRoleDTO roleDto = new DeviceRoleDTO();
        roleDto.setObjectId("1910000001");
        deviceDto.setDeviceRole(roleDto);
        deviceDto.setName(DEVICE_NAME);
        return deviceDto;
    }

}
