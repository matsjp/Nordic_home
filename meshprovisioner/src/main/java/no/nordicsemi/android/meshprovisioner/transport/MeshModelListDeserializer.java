package no.nordicsemi.android.meshprovisioner.transport;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.meshprovisioner.models.SigModelParser;
import no.nordicsemi.android.meshprovisioner.models.VendorModel;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.meshprovisioner.utils.PublicationSettings;

/**
 * Class for deserializing a list of elements stored in the Mesh Configuration Database
 */
public final class MeshModelListDeserializer implements JsonSerializer<List<MeshModel>>, JsonDeserializer<List<MeshModel>> {

    @Override
    public List<MeshModel> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final List<MeshModel> meshModels = new ArrayList<>();
        final JsonArray jsonArray = json.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            final int modelId = Integer.parseInt(jsonObject.get("modelId").getAsString(), 16);

            final PublicationSettings publicationSettings = getPublicationSettings(jsonObject);
            final List<byte[]> subscriptionAddresses = getSubscriptionAddresses(jsonObject);
            final List<Integer> boundKeyIndexes = getBoundAppKeyIndexes(jsonObject);
            final MeshModel meshModel = getMeshModel(modelId);
            if (meshModel != null) {
                meshModel.mPublicationSettings = publicationSettings;
                meshModel.mSubscriptionAddress.addAll(subscriptionAddresses);
                meshModel.mBoundAppKeyIndexes.addAll(boundKeyIndexes);
                meshModels.add(meshModel);
            }
        }
        return meshModels;
    }

    @Override
    public JsonElement serialize(final List<MeshModel> models, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonArray jsonArray = new JsonArray();
        for (MeshModel model : models) {
            final JsonObject meshModelJson = new JsonObject();
            if (model instanceof VendorModel) {
                meshModelJson.addProperty("modelId", String.format(Locale.US, "%08X", model.getModelId()));
            } else {
                meshModelJson.addProperty("modelId", String.format(Locale.US, "%04X", model.getModelId()));
            }
            if (!model.getSubscriptionAddresses().isEmpty()) {
                meshModelJson.add("subscribe", serializeSubscriptionAddresses(model.getSubscriptionAddresses()));
            }
            if (model.getPublicationSettings() != null) {
                meshModelJson.add("publish", serializePublicationSettings(model.getPublicationSettings()));
            }

            if (!model.getBoundAppKeyIndexes().isEmpty()) {
                meshModelJson.add("bind", serializeBoundAppKeys(model.getBoundAppKeyIndexes()));
            }
            jsonArray.add(meshModelJson);
        }
        return jsonArray;
    }

    /**
     * Get publication settings from json
     *
     * @param jsonObject json object
     * @return {@link PublicationSettings}
     */
    private PublicationSettings getPublicationSettings(final JsonObject jsonObject) {
        if (!jsonObject.has("publish"))
            return null;

        final JsonObject publish = jsonObject.get("publish").getAsJsonObject();
        //final int address = Integer.parseInt(publish.get("address").getAsString(), 16);
        final byte[] publishAddress = MeshParserUtils.toByteArray(publish.get("address").getAsString());//AddressUtils.getUnicastAddressBytes(address);

        final int index = publish.get("index").getAsInt();
        final int ttl = publish.get("ttl").getAsByte();

        //Unpack publish period
        final int period = publish.get("period").getAsInt();
        final int publicationSteps = period >> 6;
        final int publicationResolution = period & 0x03;

        final int publishRetransmitCount = publish.get("retransmit").getAsJsonObject().get("count").getAsInt();
        final int publishRetransmitIntervalSteps = publish.get("retransmit").getAsJsonObject().get("interval").getAsInt();

        final boolean credentials = publish.get("credentials").getAsInt() == 1;

        //Set the values
        final PublicationSettings publicationSettings = new PublicationSettings();
        publicationSettings.setPublishAddress(publishAddress);
        publicationSettings.setPublishTtl(ttl);
        publicationSettings.setPublicationSteps(publicationSteps);
        publicationSettings.setPublicationResolution(publicationResolution);
        publicationSettings.setPublishRetransmitCount(publishRetransmitCount);
        publicationSettings.setPublishRetransmitIntervalSteps(publishRetransmitIntervalSteps);
        publicationSettings.setCredentialFlag(credentials);

        return publicationSettings;
    }

    /**
     * Returns subscription addresses from json
     *
     * @param jsonObject json
     * @return list of subscription addresses
     */
    private List<byte[]> getSubscriptionAddresses(final JsonObject jsonObject) {
        final List<byte[]> subscriptions = new ArrayList<>();
        if (!(jsonObject.has("subscribe")))
            return subscriptions;

        final JsonArray jsonArray = jsonObject.get("subscribe").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            //final int address = Integer.parseInt(jsonArray.get(i).getAsString(), 16);
            final byte[] publishAddress = MeshParserUtils.toByteArray(jsonArray.get(i).getAsString());
            subscriptions.add(publishAddress);
        }
        return subscriptions;
    }

    private List<Integer> getBoundAppKeyIndexes(final JsonObject jsonObject) {
        final List<Integer> boundKeyIndexes = new ArrayList<>();
        if (!(jsonObject.has("bind")))
            return boundKeyIndexes;

        final JsonArray jsonArray = jsonObject.get("bind").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            final int index = Integer.parseInt(jsonArray.get(i).getAsString(), 16);
            boundKeyIndexes.add(index);
        }
        return boundKeyIndexes;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param subscriptions subscriptions list
     */
    private JsonArray serializeSubscriptionAddresses(final List<byte[]> subscriptions) {
        final JsonArray subscriptionsJson = new JsonArray();
        for (byte[] address : subscriptions) {
            subscriptionsJson.add(MeshParserUtils.bytesToHex(address, false));
        }
        return subscriptionsJson;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param publicationSettings publication settings for this node
     */
    private JsonObject serializePublicationSettings(final PublicationSettings publicationSettings) {
        final JsonObject publicationJson = new JsonObject();
        publicationJson.addProperty("address", MeshParserUtils.bytesToHex(publicationSettings.getPublishAddress(), false));
        publicationJson.addProperty("index", String.format(Locale.US, "%04X", publicationSettings.getAppKeyIndex()));
        publicationJson.addProperty("ttl", publicationSettings.getPublishTtl());
        publicationJson.addProperty("period", publicationSettings.calculatePublicationPeriod());

        final JsonObject retransmitJson = new JsonObject();
        retransmitJson.addProperty("count", publicationSettings.getPublishRetransmitCount());
        retransmitJson.addProperty("interval", publicationSettings.getPublishRetransmitIntervalSteps());
        publicationJson.add("retransmit", retransmitJson);
        publicationJson.addProperty("credentials", publicationSettings.getCredentialFlag() ? 1 : 0);
        return publicationJson;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param boundAppKeys List of bound app key indexes
     */
    private JsonArray serializeBoundAppKeys(final List<Integer> boundAppKeys) {
        final JsonArray boundAppKeyIndexes = new JsonArray();
        for (Integer index : boundAppKeys) {
            boundAppKeyIndexes.add(String.format(Locale.US, "%04X", index));
        }
        return boundAppKeyIndexes;
    }

    /**
     * Returns a {@link MeshModel}
     *
     * @param modelId model Id
     * @return {@link MeshModel}
     */
    private MeshModel getMeshModel(final int modelId) {
        if (modelId < Short.MIN_VALUE || modelId > Short.MAX_VALUE) {
            return new VendorModel(modelId);
        } else {
            return SigModelParser.getSigModel(modelId);
        }
    }
}
