// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Joystick; // We will replace this with XboxController
import edu.wpi.first.wpilibj.XboxController; // Import for XboxController
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // For debugging output

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private final DifferentialDrive m_robotDrive;
  // Change from two Joystick objects to one XboxController object
  private final XboxController m_driverController;

  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(1);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(3);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(4);

  /** Called once at the beginning of the robot program. */
  public Robot() {
    m_leftFront.setNeutralMode(NeutralMode.Brake);
    m_rightFront.setNeutralMode(NeutralMode.Brake);
    m_leftBack.setNeutralMode(NeutralMode.Brake);
    m_rightBack.setNeutralMode(NeutralMode.Brake);

    // Configure follower motors
    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);

    // --- CRITICAL MOTOR INVERSION SETUP ---
    // This logic remains the same. Test these points after deploying!

    // Try this first: Invert the right master. This is the most common setup.
    m_rightFront.setInverted(true);

    // IMPORTANT: Individual Motor Reversal (if needed after main side inversion)
    // m_leftFront.setInverted(true);
    // m_leftBack.setInverted(true);
    // m_rightBack.setInverted(true);

    m_robotDrive = new DifferentialDrive(m_leftFront, m_rightFront);

    // Initialize the XboxController. Assuming it's on USB port 0.
    m_driverController = new XboxController(0);

    SendableRegistry.addChild(m_robotDrive, m_leftFront);
    SendableRegistry.addChild(m_robotDrive, m_rightFront);
    SendableRegistry.addChild(m_robotDrive, m_leftBack);
    SendableRegistry.addChild(m_robotDrive, m_rightBack);

    m_robotDrive.setSafetyEnabled(false); // Set to true for production robots!
    m_robotDrive.setExpiration(0.1);
    m_robotDrive.setMaxOutput(1.0);
  }

  @Override
  public void teleopPeriodic() {
    // Read the Y-axis values from the LEFT and RIGHT analog sticks of the Xbox controller
    // XboxController.getLeftY() and getRightY() return values from -1.0 (forward) to 1.0 (backward)
    // This is different from Joystick.getY() which is usually positive downwards for USB joysticks.
    // So, we might not need the negative sign here, but we'll re-evaluate based on testing.
    double leftY = m_driverController.getLeftY();
    double rightY = m_driverController.getRightY();

    // Display joystick values for direct observation
    SmartDashboard.putNumber("Left Stick Y (Xbox)", leftY);
    SmartDashboard.putNumber("Right Stick Y (Xbox)", rightY);

    // Display current motor outputs
    SmartDashboard.putNumber("LeftFront Output", m_leftFront.getMotorOutputPercent());
    SmartDashboard.putNumber("RightFront Output", m_rightFront.getMotorOutputPercent());
    SmartDashboard.putNumber("LeftBack Output", m_leftBack.getMotorOutputPercent());
    SmartDashboard.putNumber("RightBack Output", m_rightBack.getMotorOutputPercent());

    // Drive the robot.
    // IMPORTANT: Test the signs here!
    // If pushing the stick FORWARD makes the robot go FORWARD, use `leftY, rightY`.
    // If pushing the stick FORWARD makes the robot go BACKWARD, use `-leftY, -rightY`.
    // XboxController.getLeftY() and getRightY() typically return -1.0 for full forward,
    // so using them directly might mean pushing forward makes the robot go backward.
    // The negative sign is commonly needed for tankDrive with XboxController Y axes.
    m_robotDrive.tankDrive(-leftY, -rightY);
  }
}