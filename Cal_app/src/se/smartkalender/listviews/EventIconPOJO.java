package se.smartkalender.listviews;

public class EventIconPOJO {

	private int viewType;
	private String imagePath;
 	private Integer iconId;

	public void setIconId(Integer iconId) {
		this.iconId = iconId;
	}

	public Integer getIconId() {
		return iconId;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public int getViewType() {
		return viewType;
	}

}
