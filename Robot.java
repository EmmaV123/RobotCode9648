// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

//imports for basic robot stuff and smartdashboard
package frc.robot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//imports for motor controllers, timer, xbox controller, autonomous
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.util.sendable.SendableRegistry;

//camera server stuff
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Goes across line"; 
  private static final String kSpeakerShoot = "Speaker Middle and Backup";
  private static final String kDriveShoot = "Drives forward and fires";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //left & right motor controllers mapped to left and right drive
  private final PWMSparkMax m_rightDrive = new PWMSparkMax(0);
  private final PWMSparkMax m_leftDrive = new PWMSparkMax(1);
  private final DifferentialDrive m_robotDrive =
      new DifferentialDrive(m_leftDrive::set, m_rightDrive::set);

  //motor controller for launcher
  private final PWMSparkMax launchWheel = new PWMSparkMax(5);

  private final Timer timer1 = new Timer();

  public Robot() {
    SendableRegistry.addChild(m_robotDrive, m_leftDrive);
    SendableRegistry.addChild(m_robotDrive, m_rightDrive);
    CameraServer.startAutomaticCapture();
  }

  private final XboxController driverController = new XboxController(0);

  //some variables for teleop
  double launchPower = 0;
  double drivePower = 1;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    //autonomous stuff
    m_chooser.setDefaultOption("Drives Forward and Shoots", kDriveShoot);
    m_chooser.addOption("goes forward", kDefaultAuto);
    m_chooser.addOption("goes forward", kDefaultAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    Shuffleboard.getTab("user tab").add(m_chooser);


    //certain motors inverted so the robot works properly
    launchWheel.setInverted(true);
    m_rightDrive.setInverted(true);
    timer1.start();
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);

    timer1.reset();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  //default  drives forward at half speed for 5 seconds
    if (m_autoSelected == kDefaultAuto){
      if (timer1.get() < 5.0){
      m_robotDrive.arcadeDrive(.5, 0);
    } else {
      m_robotDrive.arcadeDrive(0,0);
    }
      } 

  // DriveShoot goes forward at half speed for 5 seconds, then launches pipe
    if (m_autoSelected == kDriveShoot){
      if (timer1.get() < 5.0){
          m_robotDrive.arcadeDrive(.5, 0);
        } else if (timer1.get() < 7.0){
          m_robotDrive.arcadeDrive(0,0);
          launchWheel.set(-0.3);
        } else {
        m_robotDrive.arcadeDrive(0,0);
        launchWheel.set(0);
        }
  //old code, maybe can be reused? 
    if (m_autoSelected == kSpeakerShoot){
      while (timer1.get() < 1 ){
        launchWheel.set(1);
        }
      if (timer1.get() > 4 && timer1.get() < 9){
        launchWheel.set(0);
        m_robotDrive.arcadeDrive(-.5, 0);
      } else {
      m_robotDrive.arcadeDrive(0,0);
      }
    }
  } 
}
  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

  }
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //drive train code
    m_robotDrive.arcadeDrive((-driverController.getLeftY()*drivePower), (driverController.getRightX()*drivePower));
    //launcher code, b button launches
    if (driverController.getAButton() == true){
      timer1.reset();
      launchPower = -.28;
    //} else {
      //if (driverController.getBButton() == true ){
        //timer1.reset();
        //while (timer1.get() < 0.8 ){
        //launchPower = 1;
        //launchWheel.set(launchPower);
    }
    if (driverController.getLeftBumperButton() == true){
      drivePower = 0.6;
    } else {
      drivePower = 1;
    }
    //makes sure that the motor stops after a few seconds
    if (timer1.get() > 3.0) {
      launchPower = 0;
      timer1.reset();
    }
    launchWheel.set(launchPower);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}