package com.skarbo.campusguide.mapper.test.handler;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.android.gms.internal.b;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.skarbo.campusguide.MainActivity;
import com.skarbo.campusguide.TestActivity;
import com.skarbo.campusguide.handler.CampusguideHandler;
import com.skarbo.campusguide.model.Building;
import com.skarbo.campusguide.model.Element;
import com.skarbo.campusguide.model.Floor;
import com.skarbo.campusguide.util.CanvasUtil;
import com.skarbo.campusguide.util.Coordinate;
import com.skarbo.campusguide.util.Coordinates;
import com.skarbo.campusguide.util.Util;

public class CampusguideHandlerTest extends ActivityInstrumentationTestCase2<TestActivity> {

	private CampusguideHandler campusguideHandler;
	private String restBuildingContent;
	private String restElementContent;
	private String restFloorContent;

	public CampusguideHandlerTest() {
		super(TestActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		this.campusguideHandler = new CampusguideHandler(getActivity(), 3);
		this.restBuildingContent = Util.retrieveContent(getInstrumentation().getContext().getAssets()
				.open("content_rest_building.json"));
		this.restElementContent = Util.retrieveContent(getInstrumentation().getContext().getAssets()
				.open("content_rest_element.json"));
		this.restFloorContent = Util.retrieveContent(getInstrumentation().getContext().getAssets()
				.open("content_rest_floor.json"));
	}

	public void _testShouldRestParseBuilding() throws JSONException {
		Building.BuildingFactory buildingFactory = new Building.BuildingFactory();

		JSONObject jsonObject = new JSONObject(this.restBuildingContent);
		Building building = buildingFactory.generate(jsonObject.getJSONObject("single"));
		Log.d("TEST", building.toString());
		assertEquals("Test Building", building.getName());
		assertEquals(645, building.getId());
		assertEquals(777, building.getFacilityId());
		assertEquals(Arrays.toString(new Double[] { 60.38426, 5.33299 }), Arrays.toString(building.getLocation()));
		assertEquals(Arrays.toString(new Double[] { 60.38457, 5.33367 }), Arrays.toString(building.getPosition()));
		assertEquals(Arrays.toString(new String[] { "i|poJisp_@~@k@e@kB{@TCs@lAOv@dCT]Ll@}@jA_Ah@" }),
				Arrays.toString(building.getOverlay()));
		assertEquals(2, building.getFloors());
	}

	public void _testShouldRestParseElement() throws JSONException {
		Element.ElementFactory elementFactory = new Element.ElementFactory();

		JSONObject jsonObject = new JSONObject(this.restElementContent);
		Element element = elementFactory.generate(jsonObject.getJSONObject("single"));

		assertNotNull(element);

		Log.d("TEST", element.toString());

		assertEquals(4, element.getId());
		assertEquals("cafeteria", element.getType());
		assertEquals("room", element.getTypeGroup());
		assertEquals(55, element.getFloorId());
		assertEquals("Test", element.getName());
	}

	public void _testShouldRestParseFloor() throws JSONException {
		Floor.FloorFactory floorFactory = new Floor.FloorFactory();

		JSONObject jsonObject = new JSONObject(this.restFloorContent);
		Floor floor = floorFactory.generate(jsonObject.getJSONObject("single"));

		assertNotNull(floor);

		assertEquals(55, floor.getId());
		assertEquals(645, floor.getBuildingId());
		assertEquals("Test Floor", floor.getName());

		assertEquals(1, floor.getCoordinates().size());

		List<Coordinates> coordinatesList = floor.getCoordinates();
		assertEquals(1, coordinatesList.size());

		Coordinates coordinates = coordinatesList.get(0);
		assertEquals(4, coordinates.getCoordinates().size());
		assertEquals(2, coordinates.getCenter().length);

		PointF coordinate = coordinates.getCoordinates().get(0);
		assertEquals(43.1f, coordinate.x);
		assertEquals(31.7f, coordinate.y);

		assertEquals(123f, coordinates.getCenterPoint().x);
		assertEquals(456f, coordinates.getCenterPoint().y);

	}

	public void testShouldHitElementPolygon() throws JSONException {
		Element.ElementFactory elementFactory = new Element.ElementFactory();

		JSONObject jsonObject = new JSONObject(this.restElementContent);
		Element element = elementFactory.generate(jsonObject.getJSONObject("single"));

		List<PointF> coordinates = element.getCoordinates().get(0).getCoordinates();

		assertEquals(coordinates.size(), 4);
		
		float[] polyX = new float[] { 0f, 0f, 100f, 100f };
		float[] polyY = new float[] { 0f, 100f, 100f, 0f };
		int polySides = 4;
		float x = 50f;
		float y = 50f;
		boolean contains = CanvasUtil.polygonContains(polyX, polyY, polySides, x, y);
		assertEquals(true, contains);
		
		boolean polygonHit = CanvasUtil.polygonContains(coordinates, new PointF(0, 0));
		assertEquals(false, polygonHit);

		polygonHit = CanvasUtil.polygonContains(coordinates, new PointF(260f, 214f));
		assertEquals(true, polygonHit);

		polygonHit = CanvasUtil.polygonContains(coordinates, new PointF(600f, 500f));
		assertEquals(false, polygonHit);

		// "coordinates":[
		// [
		// "259.9",
		// "213.8"
		// ],
		// [
		// "525.9",
		// "213.8"
		// ],
		// [
		// "525.9",
		// "445.8"
		// ],
		// [
		// "259.9",
		// "445.8"
		// ]
	}
}
