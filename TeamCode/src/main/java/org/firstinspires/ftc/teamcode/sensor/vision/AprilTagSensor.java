package org.firstinspires.ftc.teamcode.sensor.vision;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Config
public class AprilTagSensor {
    // Distance from camera lens to the middle of the drivetrain (inches)
    public static final Vector2d offset = new Vector2d(0, -7.5);
    public static final int STEP_SIZE = 100;
    public static final AprilTagLibrary library = getCenterStageTagLibrary();

    /**
     * Common calibrations can be found under <a href="https://github.com/marvelsatjiar/ftc-24064-2024/blob/75de9c200b0a6623cd4f7505753be39556633b36/TeamCode/src/main/res/xml/teamwebcamcalibrations.xml">teamwebcamcalibrations.xml</a>. This calibration was done with mrcal at 640x480 on a C920. More information about common calibrating tools can be found here: <a href="https://ftc-docs.firstinspires.org/en/latest/programming_resources/vision/camera_calibration/camera-calibration.html">Camera Calibration for FIRST Tech Challenge</a>
     */
    public static final double
            fx = 693.5217154,
            fy = 687.7523885,
            cx = 326.6183680,
            cy = 243.0307655;

    public final VisionPortal visionPortal;
    private final AprilTagProcessor aprilTagProcessor;

    private int loops = STEP_SIZE;

    public AprilTagSensor(HardwareMap hardwareMap) {
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(library)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setLensIntrinsics(fx, fy, cx, cy)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .enableLiveView(true)
                .addProcessor(aprilTagProcessor)
                .build();
    }

    public List<AprilTagDetection> getRawDetections() {
        return aprilTagProcessor.getDetections();
    }

    // Needs to be updated to use any tag location, not CenterStage specific
    public Pose2d getPoseEstimate() {
        Pose2d estimate = null;
        if (loops >= STEP_SIZE) {
            for (AprilTagDetection detection : getRawDetections()) {
                if (detection.metadata != null) {
                    boolean isAudienceSideTag = detection.metadata.id >= 7;
                    int multiplier = isAudienceSideTag ? 1 : -1;
                    VectorF tagVec = library.lookupTag(detection.id).fieldPosition;
                    estimate = new Pose2d(
                            tagVec.get(0) + (detection.ftcPose.y - offset.y) * multiplier,
                            tagVec.get(1) + (detection.ftcPose.x - offset.x) * -multiplier,
                            Math.toRadians((isAudienceSideTag ? 0 : 180) + detection.ftcPose.yaw)
                    );
                }
            }
            loops = 0;
        } else {
            loops++;
        }

        return estimate;
    }

    // Credit to @overkil of team 14343
    public static AprilTagLibrary getCenterStageTagLibrary() {
        return new AprilTagLibrary.Builder()
                .addTag(1, "BlueAllianceLeft",
                        2, new VectorF(61.75f, 41.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(2, "BlueAllianceCenter",
                        2, new VectorF(61.75f, 35.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(3, "BlueAllianceRight",
                        2, new VectorF(61.75f, 29.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(4, "RedAllianceLeft",
                        2, new VectorF(61.75f, -29.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(5, "RedAllianceCenter",
                        2, new VectorF(61.75f, -35.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(6, "RedAllianceRight",
                        2, new VectorF(61.75f, -41.41f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.3536f, -0.6124f, 0.6124f, -0.3536f, 0))
                .addTag(7, "RedAudienceWallLarge",
                        5, new VectorF(-70.25f, -40.625f, 5.5f), DistanceUnit.INCH,
                        new Quaternion(0.5f, -0.5f, -0.5f, 0.5f, 0))
                .addTag(8, "RedAudienceWallSmall",
                        2, new VectorF(-70.25f, -35.125f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.5f, -0.5f, -0.5f, 0.5f, 0))
                .addTag(9, "BlueAudienceWallSmall",
                        2, new VectorF(-70.25f, 35.125f, 4f), DistanceUnit.INCH,
                        new Quaternion(0.5f, -0.5f, -0.5f, 0.5f, 0))
                .addTag(10, "BlueAudienceWallLarge",
                        5, new VectorF(-70.25f, 40.625f, 5.5f), DistanceUnit.INCH,
                        new Quaternion(0.5f, -0.5f, -0.5f, 0.5f, 0))
                .build();
    }
}
