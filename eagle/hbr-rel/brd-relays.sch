<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE eagle SYSTEM "eagle.dtd">
<eagle version="6.5.0">
<drawing>
<settings>
<setting alwaysvectorfont="no"/>
<setting verticaltext="up"/>
</settings>
<grid distance="0.1" unitdist="inch" unit="inch" style="dots" multiple="1" display="yes" altdistance="0.01" altunitdist="inch" altunit="inch"/>
<layers>
<layer number="1" name="Top" color="4" fill="1" visible="no" active="no"/>
<layer number="16" name="Bottom" color="1" fill="1" visible="no" active="no"/>
<layer number="17" name="Pads" color="2" fill="1" visible="no" active="no"/>
<layer number="18" name="Vias" color="2" fill="1" visible="no" active="no"/>
<layer number="19" name="Unrouted" color="6" fill="1" visible="no" active="no"/>
<layer number="20" name="Dimension" color="15" fill="1" visible="no" active="no"/>
<layer number="21" name="tPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="22" name="bPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="23" name="tOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="24" name="bOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="25" name="tNames" color="7" fill="1" visible="no" active="no"/>
<layer number="26" name="bNames" color="7" fill="1" visible="no" active="no"/>
<layer number="27" name="tValues" color="7" fill="1" visible="no" active="no"/>
<layer number="28" name="bValues" color="7" fill="1" visible="no" active="no"/>
<layer number="29" name="tStop" color="7" fill="3" visible="no" active="no"/>
<layer number="30" name="bStop" color="7" fill="6" visible="no" active="no"/>
<layer number="31" name="tCream" color="7" fill="4" visible="no" active="no"/>
<layer number="32" name="bCream" color="7" fill="5" visible="no" active="no"/>
<layer number="33" name="tFinish" color="6" fill="3" visible="no" active="no"/>
<layer number="34" name="bFinish" color="6" fill="6" visible="no" active="no"/>
<layer number="35" name="tGlue" color="7" fill="4" visible="no" active="no"/>
<layer number="36" name="bGlue" color="7" fill="5" visible="no" active="no"/>
<layer number="37" name="tTest" color="7" fill="1" visible="no" active="no"/>
<layer number="38" name="bTest" color="7" fill="1" visible="no" active="no"/>
<layer number="39" name="tKeepout" color="4" fill="11" visible="no" active="no"/>
<layer number="40" name="bKeepout" color="1" fill="11" visible="no" active="no"/>
<layer number="41" name="tRestrict" color="4" fill="10" visible="no" active="no"/>
<layer number="42" name="bRestrict" color="1" fill="10" visible="no" active="no"/>
<layer number="43" name="vRestrict" color="2" fill="10" visible="no" active="no"/>
<layer number="44" name="Drills" color="7" fill="1" visible="no" active="no"/>
<layer number="45" name="Holes" color="7" fill="1" visible="no" active="no"/>
<layer number="46" name="Milling" color="3" fill="1" visible="no" active="no"/>
<layer number="47" name="Measures" color="7" fill="1" visible="no" active="no"/>
<layer number="48" name="Document" color="7" fill="1" visible="no" active="no"/>
<layer number="49" name="Reference" color="7" fill="1" visible="no" active="no"/>
<layer number="51" name="tDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="52" name="bDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="91" name="Nets" color="2" fill="1" visible="yes" active="yes"/>
<layer number="92" name="Busses" color="1" fill="1" visible="yes" active="yes"/>
<layer number="93" name="Pins" color="2" fill="1" visible="no" active="yes"/>
<layer number="94" name="Symbols" color="4" fill="1" visible="yes" active="yes"/>
<layer number="95" name="Names" color="7" fill="1" visible="yes" active="yes"/>
<layer number="96" name="Values" color="7" fill="1" visible="yes" active="yes"/>
<layer number="97" name="Info" color="7" fill="1" visible="yes" active="yes"/>
<layer number="98" name="Guide" color="6" fill="1" visible="yes" active="yes"/>
</layers>
<schematic xreflabel="%F%N/%S.%C%R" xrefpart="/%S.%C%R">
<libraries>
<library name="dbg-misc">
<packages>
<package name="CB1">
<description>&lt;b&gt;HIGH POWER AUTOMOTIVE RELAY&lt;/b&gt; NAiS&lt;p&gt;
Source: http://www.mew-europe.com/..  en_ds_61202_0000.pdf</description>
<wire x1="-12.4" y1="10.15" x2="-12.4" y2="-16.15" width="0.2032" layer="21"/>
<wire x1="-11.65" y1="-16.9" x2="16.65" y2="-16.9" width="0.2032" layer="21"/>
<wire x1="17.4" y1="-16.15" x2="17.4" y2="10.15" width="0.2032" layer="21"/>
<wire x1="16.65" y1="10.9" x2="-11.65" y2="10.9" width="0.2032" layer="21"/>
<wire x1="-12.4" y1="10.15" x2="-11.65" y2="10.9" width="0.2032" layer="21" curve="-90"/>
<wire x1="16.65" y1="10.9" x2="17.4" y2="10.15" width="0.2032" layer="21" curve="-90"/>
<wire x1="17.4" y1="-16.15" x2="16.65" y2="-16.9" width="0.2032" layer="21" curve="-90"/>
<wire x1="-11.65" y1="-16.9" x2="-12.4" y2="-16.15" width="0.2032" layer="21" curve="-90"/>
<pad name="30" x="9.95" y="-3" drill="2.3" rot="R180"/>
<pad name="87" x="-9.95" y="-3" drill="2.3" rot="R180"/>
<pad name="87A" x="-0.95" y="-3" drill="2.3" rot="R180"/>
<pad name="86" x="-1.05" y="5.9" drill="2.3" rot="R180"/>
<pad name="85" x="-1.05" y="-11.9" drill="2.3" rot="R180"/>
<text x="-10.16" y="3.81" size="1.27" layer="25">&gt;NAME</text>
<text x="-10.16" y="-5.08" size="1.27" layer="27">&gt;VALUE</text>
</package>
<package name="1X02">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<wire x1="-4.525" y1="3.08" x2="-0.635" y2="3.08" width="0.1524" layer="21"/>
<wire x1="-0.635" y1="3.08" x2="0" y2="2.445" width="0.1524" layer="21"/>
<wire x1="0" y1="2.445" x2="0" y2="-2.445" width="0.1524" layer="21"/>
<wire x1="0" y1="-2.445" x2="-0.635" y2="-3.08" width="0.1524" layer="21"/>
<wire x1="-5.16" y1="2.445" x2="-5.16" y2="-2.445" width="0.1524" layer="21"/>
<wire x1="-4.525" y1="3.08" x2="-5.16" y2="2.445" width="0.1524" layer="21"/>
<wire x1="-5.16" y1="-2.445" x2="-4.525" y2="-3.08" width="0.1524" layer="21"/>
<wire x1="-0.635" y1="-3.08" x2="-4.525" y2="-3.08" width="0.1524" layer="21"/>
<wire x1="0" y1="2.445" x2="0.635" y2="3.08" width="0.1524" layer="21"/>
<wire x1="0.635" y1="3.08" x2="3.525" y2="3.08" width="0.1524" layer="21"/>
<wire x1="3.525" y1="3.08" x2="4.16" y2="2.445" width="0.1524" layer="21"/>
<wire x1="4.16" y1="2.445" x2="4.16" y2="-2.445" width="0.1524" layer="21"/>
<wire x1="4.16" y1="-2.445" x2="3.525" y2="-3.08" width="0.1524" layer="21"/>
<wire x1="3.525" y1="-3.08" x2="0.635" y2="-3.08" width="0.1524" layer="21"/>
<wire x1="0.635" y1="-3.08" x2="0" y2="-2.445" width="0.1524" layer="21"/>
<pad name="1" x="-3.35" y="0" drill="1.5" shape="long" rot="R90"/>
<pad name="2" x="2.62" y="0" drill="1.5" shape="long" rot="R90"/>
<text x="-2.6162" y="1.8288" size="1.27" layer="25" ratio="10">&gt;NAME</text>
<text x="-2.54" y="-3.175" size="1.27" layer="27">&gt;VALUE</text>
<rectangle x1="-3.604" y1="-0.254" x2="-3.096" y2="0.254" layer="51"/>
<rectangle x1="2.366" y1="-0.254" x2="2.874" y2="0.254" layer="51"/>
</package>
<package name="1X02/90">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<wire x1="-2.54" y1="-1.905" x2="0" y2="-1.905" width="0.1524" layer="21"/>
<wire x1="0" y1="-1.905" x2="0" y2="0.635" width="0.1524" layer="21"/>
<wire x1="0" y1="0.635" x2="-2.54" y2="0.635" width="0.1524" layer="21"/>
<wire x1="-2.54" y1="0.635" x2="-2.54" y2="-1.905" width="0.1524" layer="21"/>
<wire x1="-1.27" y1="6.985" x2="-1.27" y2="1.27" width="0.762" layer="21"/>
<wire x1="0" y1="-1.905" x2="2.54" y2="-1.905" width="0.1524" layer="21"/>
<wire x1="2.54" y1="-1.905" x2="2.54" y2="0.635" width="0.1524" layer="21"/>
<wire x1="2.54" y1="0.635" x2="0" y2="0.635" width="0.1524" layer="21"/>
<wire x1="1.27" y1="6.985" x2="1.27" y2="1.27" width="0.762" layer="21"/>
<pad name="1" x="-1.27" y="-3.81" drill="1.016" shape="long" rot="R90"/>
<pad name="2" x="1.27" y="-3.81" drill="1.016" shape="long" rot="R90"/>
<text x="-3.175" y="-3.81" size="1.27" layer="25" ratio="10" rot="R90">&gt;NAME</text>
<text x="4.445" y="-3.81" size="1.27" layer="27" rot="R90">&gt;VALUE</text>
<rectangle x1="-1.651" y1="0.635" x2="-0.889" y2="1.143" layer="21"/>
<rectangle x1="0.889" y1="0.635" x2="1.651" y2="1.143" layer="21"/>
<rectangle x1="-1.651" y1="-2.921" x2="-0.889" y2="-1.905" layer="21"/>
<rectangle x1="0.889" y1="-2.921" x2="1.651" y2="-1.905" layer="21"/>
</package>
</packages>
<symbols>
<symbol name="K">
<wire x1="-3.81" y1="-1.905" x2="-1.905" y2="-1.905" width="0.254" layer="94"/>
<wire x1="3.81" y1="-1.905" x2="3.81" y2="1.905" width="0.254" layer="94"/>
<wire x1="3.81" y1="1.905" x2="1.905" y2="1.905" width="0.254" layer="94"/>
<wire x1="-3.81" y1="1.905" x2="-3.81" y2="-1.905" width="0.254" layer="94"/>
<wire x1="0" y1="-2.54" x2="0" y2="-1.905" width="0.1524" layer="94"/>
<wire x1="0" y1="-1.905" x2="3.81" y2="-1.905" width="0.254" layer="94"/>
<wire x1="0" y1="2.54" x2="0" y2="1.905" width="0.1524" layer="94"/>
<wire x1="0" y1="1.905" x2="-3.81" y2="1.905" width="0.254" layer="94"/>
<wire x1="-1.905" y1="-1.905" x2="1.905" y2="1.905" width="0.1524" layer="94"/>
<wire x1="-1.905" y1="-1.905" x2="0" y2="-1.905" width="0.254" layer="94"/>
<wire x1="1.905" y1="1.905" x2="0" y2="1.905" width="0.254" layer="94"/>
<text x="1.27" y="2.921" size="1.778" layer="96">&gt;VALUE</text>
<text x="1.27" y="5.08" size="1.778" layer="95">&gt;PART</text>
<pin name="2" x="0" y="-5.08" visible="pad" length="short" direction="pas" rot="R90"/>
<pin name="1" x="0" y="5.08" visible="pad" length="short" direction="pas" rot="R270"/>
</symbol>
<symbol name="U">
<wire x1="3.175" y1="5.08" x2="1.905" y2="5.08" width="0.254" layer="94"/>
<wire x1="-3.175" y1="5.08" x2="-1.905" y2="5.08" width="0.254" layer="94"/>
<wire x1="0" y1="1.27" x2="2.54" y2="5.715" width="0.254" layer="94"/>
<wire x1="0" y1="1.27" x2="0" y2="0" width="0.254" layer="94"/>
<circle x="0" y="1.27" radius="0.127" width="0.4064" layer="94"/>
<text x="2.54" y="0" size="1.778" layer="95">&gt;PART</text>
<pin name="O" x="5.08" y="5.08" visible="pad" length="short" direction="pas" rot="R180"/>
<pin name="S" x="-5.08" y="5.08" visible="pad" length="short" direction="pas"/>
<pin name="P" x="0" y="-2.54" visible="pad" length="short" direction="pas" rot="R90"/>
</symbol>
<symbol name="PINHD2">
<wire x1="-6.35" y1="-2.54" x2="1.27" y2="-2.54" width="0.4064" layer="94"/>
<wire x1="1.27" y1="-2.54" x2="1.27" y2="5.08" width="0.4064" layer="94"/>
<wire x1="1.27" y1="5.08" x2="-6.35" y2="5.08" width="0.4064" layer="94"/>
<wire x1="-6.35" y1="5.08" x2="-6.35" y2="-2.54" width="0.4064" layer="94"/>
<text x="-6.35" y="5.715" size="1.778" layer="95">&gt;NAME</text>
<text x="-6.35" y="-5.08" size="1.778" layer="96">&gt;VALUE</text>
<pin name="1" x="-2.54" y="2.54" visible="pad" length="short" direction="pas" function="dot"/>
<pin name="2" x="-2.54" y="0" visible="pad" length="short" direction="pas" function="dot"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="CB1*" prefix="K">
<description>&lt;b&gt;HIGH POWER AUTOMOTIVE RELAY Form C&lt;/b&gt; NAiS&lt;p&gt;
Source: http://www.mew-europe.com/..  en_ds_61202_0000.pdf</description>
<gates>
<gate name="1" symbol="K" x="-7.62" y="0" addlevel="must"/>
<gate name="2" symbol="U" x="10.16" y="0" addlevel="always"/>
</gates>
<devices>
<device name="" package="CB1">
<connects>
<connect gate="1" pin="1" pad="86"/>
<connect gate="1" pin="2" pad="85"/>
<connect gate="2" pin="O" pad="87A"/>
<connect gate="2" pin="P" pad="30"/>
<connect gate="2" pin="S" pad="87"/>
</connects>
<technologies>
<technology name="-P-12V">
<attribute name="MF" value="AROMAT/ MATSUSHITA" constant="no"/>
<attribute name="MPN" value="CB1-P-12V" constant="no"/>
<attribute name="OC_FARNELL" value="unknown" constant="no"/>
<attribute name="OC_NEWARK" value="52F9201" constant="no"/>
</technology>
<technology name="-P-24V">
<attribute name="MF" value="AROMAT/ MATSUSHITA" constant="no"/>
<attribute name="MPN" value="CB1-P-24V" constant="no"/>
<attribute name="OC_FARNELL" value="4168987" constant="no"/>
<attribute name="OC_NEWARK" value="07C0827" constant="no"/>
</technology>
<technology name="F-P-12V">
<attribute name="MF" value="AROMAT/ MATSUSHITA" constant="no"/>
<attribute name="MPN" value="CB1F-P-12V" constant="no"/>
<attribute name="OC_FARNELL" value="1414069" constant="no"/>
<attribute name="OC_NEWARK" value="50F4024" constant="no"/>
</technology>
<technology name="F-P-24V">
<attribute name="MF" value="AROMAT/ MATSUSHITA" constant="no"/>
<attribute name="MPN" value="CB1F-P-24V" constant="no"/>
<attribute name="OC_FARNELL" value="1423191" constant="no"/>
<attribute name="OC_NEWARK" value="94B6706" constant="no"/>
</technology>
</technologies>
</device>
</devices>
</deviceset>
<deviceset name="PINHD-1X2" prefix="JP" uservalue="yes">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<gates>
<gate name="G$1" symbol="PINHD2" x="0" y="0"/>
</gates>
<devices>
<device name="" package="1X02">
<connects>
<connect gate="G$1" pin="1" pad="1"/>
<connect gate="G$1" pin="2" pad="2"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
<device name="/90" package="1X02/90">
<connects>
<connect gate="G$1" pin="1" pad="1"/>
<connect gate="G$1" pin="2" pad="2"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
<device name="2X1-BIG" package="1X02">
<connects>
<connect gate="G$1" pin="1" pad="1"/>
<connect gate="G$1" pin="2" pad="2"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
<library name="diode">
<description>&lt;b&gt;Diodes&lt;/b&gt;&lt;p&gt;
Based on the following sources:
&lt;ul&gt;
&lt;li&gt;Motorola : www.onsemi.com
&lt;li&gt;Fairchild : www.fairchildsemi.com
&lt;li&gt;Philips : www.semiconductors.com
&lt;li&gt;Vishay : www.vishay.de
&lt;/ul&gt;
&lt;author&gt;Created by librarian@cadsoft.de&lt;/author&gt;</description>
<packages>
<package name="DO41-10">
<description>&lt;B&gt;DIODE&lt;/B&gt;&lt;p&gt;
diameter 2.54 mm, horizontal, grid 10.16 mm</description>
<wire x1="2.032" y1="-1.27" x2="-2.032" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="2.032" y1="-1.27" x2="2.032" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-2.032" y1="1.27" x2="2.032" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-2.032" y1="1.27" x2="-2.032" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="5.08" y1="0" x2="4.064" y2="0" width="0.762" layer="51"/>
<wire x1="-5.08" y1="0" x2="-4.064" y2="0" width="0.762" layer="51"/>
<wire x1="-0.635" y1="0" x2="0" y2="0" width="0.1524" layer="21"/>
<wire x1="1.016" y1="0.635" x2="1.016" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="1.016" y1="-0.635" x2="0" y2="0" width="0.1524" layer="21"/>
<wire x1="0" y1="0" x2="1.524" y2="0" width="0.1524" layer="21"/>
<wire x1="0" y1="0" x2="1.016" y2="0.635" width="0.1524" layer="21"/>
<wire x1="0" y1="0.635" x2="0" y2="0" width="0.1524" layer="21"/>
<wire x1="0" y1="0" x2="0" y2="-0.635" width="0.1524" layer="21"/>
<pad name="A" x="5.08" y="0" drill="1.1176"/>
<pad name="C" x="-5.08" y="0" drill="1.1176"/>
<text x="-2.032" y="1.651" size="1.27" layer="25" ratio="10">&gt;NAME</text>
<text x="-2.032" y="-2.794" size="1.27" layer="27" ratio="10">&gt;VALUE</text>
<rectangle x1="-1.651" y1="-1.27" x2="-1.143" y2="1.27" layer="21"/>
<rectangle x1="2.032" y1="-0.381" x2="3.937" y2="0.381" layer="21"/>
<rectangle x1="-3.937" y1="-0.381" x2="-2.032" y2="0.381" layer="21"/>
</package>
</packages>
<symbols>
<symbol name="D">
<wire x1="-1.27" y1="-1.27" x2="1.27" y2="0" width="0.254" layer="94"/>
<wire x1="1.27" y1="0" x2="-1.27" y2="1.27" width="0.254" layer="94"/>
<wire x1="1.27" y1="1.27" x2="1.27" y2="0" width="0.254" layer="94"/>
<wire x1="-1.27" y1="1.27" x2="-1.27" y2="-1.27" width="0.254" layer="94"/>
<wire x1="1.27" y1="0" x2="1.27" y2="-1.27" width="0.254" layer="94"/>
<text x="2.54" y="0.4826" size="1.778" layer="95">&gt;NAME</text>
<text x="2.54" y="-2.3114" size="1.778" layer="96">&gt;VALUE</text>
<pin name="A" x="-2.54" y="0" visible="off" length="short" direction="pas"/>
<pin name="C" x="2.54" y="0" visible="off" length="short" direction="pas" rot="R180"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="1N4004" prefix="D">
<description>&lt;B&gt;DIODE&lt;/B&gt;&lt;p&gt;
general purpose rectifier, 1 A</description>
<gates>
<gate name="1" symbol="D" x="0" y="0"/>
</gates>
<devices>
<device name="" package="DO41-10">
<connects>
<connect gate="1" pin="A" pad="A"/>
<connect gate="1" pin="C" pad="C"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
</libraries>
<attributes>
</attributes>
<variantdefs>
</variantdefs>
<classes>
<class number="0" name="default" width="0" drill="0">
</class>
</classes>
<parts>
<part name="M1A" library="dbg-misc" deviceset="CB1*" device="" technology="-P-12V"/>
<part name="M1B" library="dbg-misc" deviceset="CB1*" device="" technology="-P-12V"/>
<part name="M2A" library="dbg-misc" deviceset="CB1*" device="" technology="-P-12V"/>
<part name="M2B" library="dbg-misc" deviceset="CB1*" device="" technology="-P-12V"/>
<part name="PWR_MAIN" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="M1" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="M2" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="RC1" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="RC2" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="JPM-PWM" library="dbg-misc" deviceset="PINHD-1X2" device="2X1-BIG"/>
<part name="DM1A" library="diode" deviceset="1N4004" device=""/>
<part name="DM1B" library="diode" deviceset="1N4004" device=""/>
<part name="DM2A" library="diode" deviceset="1N4004" device=""/>
<part name="DM2B" library="diode" deviceset="1N4004" device=""/>
<part name="RMAIN" library="dbg-misc" deviceset="CB1*" device="" technology="-P-12V"/>
<part name="DRMAIN" library="diode" deviceset="1N4004" device=""/>
</parts>
<sheets>
<sheet>
<plain>
</plain>
<instances>
<instance part="M1A" gate="1" x="22.86" y="33.02" rot="R180"/>
<instance part="M1A" gate="2" x="25.4" y="73.66" rot="R90"/>
<instance part="M1B" gate="1" x="53.34" y="33.02" rot="R180"/>
<instance part="M1B" gate="2" x="48.26" y="73.66" rot="R270"/>
<instance part="M2A" gate="1" x="96.52" y="33.02" rot="R180"/>
<instance part="M2A" gate="2" x="99.06" y="71.12" rot="R90"/>
<instance part="M2B" gate="1" x="134.62" y="33.02" rot="R180"/>
<instance part="M2B" gate="2" x="127" y="71.12" rot="R270"/>
<instance part="PWR_MAIN" gate="G$1" x="-20.32" y="71.12" rot="R180"/>
<instance part="M1" gate="G$1" x="35.56" y="66.04" rot="R180"/>
<instance part="M2" gate="G$1" x="111.76" y="66.04" rot="R180"/>
<instance part="RC1" gate="G$1" x="20.32" y="10.16" rot="R180"/>
<instance part="RC2" gate="G$1" x="111.76" y="5.08" rot="R180"/>
<instance part="JPM-PWM" gate="G$1" x="-15.24" y="43.18" rot="R180"/>
<instance part="DM1A" gate="1" x="33.02" y="33.02" rot="R90"/>
<instance part="DM1B" gate="1" x="63.5" y="33.02" rot="R90"/>
<instance part="DM2A" gate="1" x="106.68" y="33.02" rot="R90"/>
<instance part="DM2B" gate="1" x="144.78" y="35.56" rot="R90"/>
<instance part="RMAIN" gate="1" x="2.54" y="20.32" rot="R180"/>
<instance part="RMAIN" gate="2" x="2.54" y="73.66" rot="R90"/>
<instance part="DRMAIN" gate="1" x="10.16" y="22.86" rot="R90"/>
</instances>
<busses>
</busses>
<nets>
<net name="N$1" class="0">
<segment>
<pinref part="M1A" gate="2" pin="O"/>
<wire x1="20.32" y1="81.28" x2="20.32" y2="78.74" width="0.1524" layer="91"/>
<wire x1="53.34" y1="81.28" x2="20.32" y2="81.28" width="0.1524" layer="91"/>
<pinref part="M1B" gate="2" pin="S"/>
<wire x1="53.34" y1="81.28" x2="53.34" y2="78.74" width="0.1524" layer="91"/>
<pinref part="M2A" gate="2" pin="O"/>
<wire x1="53.34" y1="81.28" x2="93.98" y2="81.28" width="0.1524" layer="91"/>
<wire x1="93.98" y1="81.28" x2="93.98" y2="76.2" width="0.1524" layer="91"/>
<junction x="53.34" y="81.28"/>
<pinref part="M2B" gate="2" pin="S"/>
<wire x1="93.98" y1="81.28" x2="132.08" y2="81.28" width="0.1524" layer="91"/>
<wire x1="132.08" y1="81.28" x2="132.08" y2="76.2" width="0.1524" layer="91"/>
<junction x="93.98" y="81.28"/>
<wire x1="132.08" y1="81.28" x2="147.32" y2="81.28" width="0.1524" layer="91"/>
<wire x1="147.32" y1="81.28" x2="147.32" y2="45.72" width="0.1524" layer="91"/>
<wire x1="147.32" y1="45.72" x2="144.78" y2="45.72" width="0.1524" layer="91"/>
<junction x="132.08" y="81.28"/>
<pinref part="M1A" gate="1" pin="2"/>
<wire x1="144.78" y1="45.72" x2="134.62" y2="45.72" width="0.1524" layer="91"/>
<wire x1="134.62" y1="45.72" x2="106.68" y2="45.72" width="0.1524" layer="91"/>
<wire x1="106.68" y1="45.72" x2="96.52" y2="45.72" width="0.1524" layer="91"/>
<wire x1="96.52" y1="45.72" x2="63.5" y2="45.72" width="0.1524" layer="91"/>
<wire x1="63.5" y1="45.72" x2="53.34" y2="45.72" width="0.1524" layer="91"/>
<wire x1="53.34" y1="45.72" x2="33.02" y2="45.72" width="0.1524" layer="91"/>
<wire x1="33.02" y1="45.72" x2="22.86" y2="45.72" width="0.1524" layer="91"/>
<wire x1="22.86" y1="45.72" x2="22.86" y2="38.1" width="0.1524" layer="91"/>
<pinref part="M1B" gate="1" pin="2"/>
<wire x1="53.34" y1="45.72" x2="53.34" y2="38.1" width="0.1524" layer="91"/>
<pinref part="M2A" gate="1" pin="2"/>
<wire x1="96.52" y1="45.72" x2="96.52" y2="38.1" width="0.1524" layer="91"/>
<pinref part="M2B" gate="1" pin="2"/>
<wire x1="134.62" y1="45.72" x2="134.62" y2="38.1" width="0.1524" layer="91"/>
<junction x="53.34" y="45.72"/>
<junction x="96.52" y="45.72"/>
<junction x="134.62" y="45.72"/>
<pinref part="DM1A" gate="1" pin="C"/>
<wire x1="33.02" y1="35.56" x2="33.02" y2="45.72" width="0.1524" layer="91"/>
<junction x="33.02" y="45.72"/>
<pinref part="DM1B" gate="1" pin="C"/>
<wire x1="63.5" y1="35.56" x2="63.5" y2="45.72" width="0.1524" layer="91"/>
<junction x="63.5" y="45.72"/>
<pinref part="DM2A" gate="1" pin="C"/>
<wire x1="106.68" y1="35.56" x2="106.68" y2="45.72" width="0.1524" layer="91"/>
<pinref part="DM2B" gate="1" pin="C"/>
<wire x1="144.78" y1="38.1" x2="144.78" y2="45.72" width="0.1524" layer="91"/>
<junction x="106.68" y="45.72"/>
<junction x="144.78" y="45.72"/>
<pinref part="RMAIN" gate="2" pin="P"/>
<wire x1="5.08" y1="73.66" x2="10.16" y2="73.66" width="0.1524" layer="91"/>
<wire x1="10.16" y1="73.66" x2="10.16" y2="81.28" width="0.1524" layer="91"/>
<wire x1="10.16" y1="81.28" x2="20.32" y2="81.28" width="0.1524" layer="91"/>
<junction x="20.32" y="81.28"/>
<pinref part="RMAIN" gate="1" pin="2"/>
<wire x1="2.54" y1="25.4" x2="2.54" y2="35.56" width="0.1524" layer="91"/>
<wire x1="2.54" y1="35.56" x2="10.16" y2="35.56" width="0.1524" layer="91"/>
<wire x1="10.16" y1="35.56" x2="10.16" y2="45.72" width="0.1524" layer="91"/>
<wire x1="10.16" y1="45.72" x2="22.86" y2="45.72" width="0.1524" layer="91"/>
<junction x="22.86" y="45.72"/>
<pinref part="DRMAIN" gate="1" pin="C"/>
<wire x1="10.16" y1="25.4" x2="10.16" y2="35.56" width="0.1524" layer="91"/>
<junction x="10.16" y="35.56"/>
</segment>
</net>
<net name="N$2" class="0">
<segment>
<wire x1="40.64" y1="73.66" x2="40.64" y2="66.04" width="0.1524" layer="91"/>
<pinref part="M1" gate="G$1" pin="2"/>
<wire x1="40.64" y1="66.04" x2="38.1" y2="66.04" width="0.1524" layer="91"/>
<pinref part="M1A" gate="2" pin="P"/>
<wire x1="27.94" y1="73.66" x2="40.64" y2="73.66" width="0.1524" layer="91"/>
</segment>
</net>
<net name="N$3" class="0">
<segment>
<pinref part="M1" gate="G$1" pin="1"/>
<wire x1="38.1" y1="63.5" x2="43.18" y2="63.5" width="0.1524" layer="91"/>
<wire x1="43.18" y1="63.5" x2="43.18" y2="73.66" width="0.1524" layer="91"/>
<pinref part="M1B" gate="2" pin="P"/>
<wire x1="45.72" y1="73.66" x2="43.18" y2="73.66" width="0.1524" layer="91"/>
</segment>
</net>
<net name="N$5" class="0">
<segment>
<pinref part="M1A" gate="2" pin="S"/>
<wire x1="20.32" y1="68.58" x2="20.32" y2="53.34" width="0.1524" layer="91"/>
<wire x1="20.32" y1="53.34" x2="-5.08" y2="53.34" width="0.1524" layer="91"/>
<wire x1="-5.08" y1="53.34" x2="-5.08" y2="43.18" width="0.1524" layer="91"/>
<pinref part="JPM-PWM" gate="G$1" pin="2"/>
<wire x1="-5.08" y1="43.18" x2="-12.7" y2="43.18" width="0.1524" layer="91"/>
<pinref part="M1B" gate="2" pin="O"/>
<wire x1="53.34" y1="68.58" x2="53.34" y2="53.34" width="0.1524" layer="91"/>
<wire x1="53.34" y1="53.34" x2="20.32" y2="53.34" width="0.1524" layer="91"/>
<junction x="20.32" y="53.34"/>
</segment>
</net>
<net name="N$7" class="0">
<segment>
<pinref part="M2A" gate="2" pin="P"/>
<wire x1="101.6" y1="71.12" x2="119.38" y2="71.12" width="0.1524" layer="91"/>
<wire x1="119.38" y1="71.12" x2="119.38" y2="66.04" width="0.1524" layer="91"/>
<pinref part="M2" gate="G$1" pin="2"/>
<wire x1="119.38" y1="66.04" x2="114.3" y2="66.04" width="0.1524" layer="91"/>
</segment>
</net>
<net name="N$8" class="0">
<segment>
<pinref part="M2" gate="G$1" pin="1"/>
<wire x1="114.3" y1="63.5" x2="121.92" y2="63.5" width="0.1524" layer="91"/>
<wire x1="121.92" y1="63.5" x2="121.92" y2="71.12" width="0.1524" layer="91"/>
<pinref part="M2B" gate="2" pin="P"/>
<wire x1="121.92" y1="71.12" x2="124.46" y2="71.12" width="0.1524" layer="91"/>
</segment>
</net>
<net name="N$9" class="0">
<segment>
<pinref part="M1A" gate="1" pin="1"/>
<wire x1="22.86" y1="27.94" x2="22.86" y2="20.32" width="0.1524" layer="91"/>
<wire x1="22.86" y1="20.32" x2="33.02" y2="20.32" width="0.1524" layer="91"/>
<wire x1="33.02" y1="20.32" x2="35.56" y2="20.32" width="0.1524" layer="91"/>
<wire x1="35.56" y1="20.32" x2="35.56" y2="10.16" width="0.1524" layer="91"/>
<pinref part="RC1" gate="G$1" pin="2"/>
<wire x1="35.56" y1="10.16" x2="22.86" y2="10.16" width="0.1524" layer="91"/>
<pinref part="DM1A" gate="1" pin="A"/>
<wire x1="33.02" y1="30.48" x2="33.02" y2="20.32" width="0.1524" layer="91"/>
<junction x="33.02" y="20.32"/>
</segment>
</net>
<net name="N$10" class="0">
<segment>
<pinref part="M1B" gate="1" pin="1"/>
<wire x1="53.34" y1="27.94" x2="53.34" y2="25.4" width="0.1524" layer="91"/>
<pinref part="RC1" gate="G$1" pin="1"/>
<wire x1="53.34" y1="25.4" x2="53.34" y2="7.62" width="0.1524" layer="91"/>
<wire x1="53.34" y1="7.62" x2="22.86" y2="7.62" width="0.1524" layer="91"/>
<pinref part="DM1B" gate="1" pin="A"/>
<wire x1="63.5" y1="30.48" x2="63.5" y2="25.4" width="0.1524" layer="91"/>
<wire x1="63.5" y1="25.4" x2="53.34" y2="25.4" width="0.1524" layer="91"/>
<junction x="53.34" y="25.4"/>
</segment>
</net>
<net name="N$11" class="0">
<segment>
<pinref part="M2A" gate="1" pin="1"/>
<wire x1="96.52" y1="27.94" x2="96.52" y2="15.24" width="0.1524" layer="91"/>
<wire x1="96.52" y1="15.24" x2="106.68" y2="15.24" width="0.1524" layer="91"/>
<wire x1="106.68" y1="15.24" x2="124.46" y2="15.24" width="0.1524" layer="91"/>
<wire x1="124.46" y1="15.24" x2="124.46" y2="5.08" width="0.1524" layer="91"/>
<pinref part="RC2" gate="G$1" pin="2"/>
<wire x1="124.46" y1="5.08" x2="114.3" y2="5.08" width="0.1524" layer="91"/>
<pinref part="DM2A" gate="1" pin="A"/>
<wire x1="106.68" y1="30.48" x2="106.68" y2="15.24" width="0.1524" layer="91"/>
<junction x="106.68" y="15.24"/>
</segment>
</net>
<net name="N$12" class="0">
<segment>
<pinref part="M2B" gate="1" pin="1"/>
<wire x1="134.62" y1="27.94" x2="134.62" y2="25.4" width="0.1524" layer="91"/>
<pinref part="RC2" gate="G$1" pin="1"/>
<wire x1="134.62" y1="25.4" x2="134.62" y2="2.54" width="0.1524" layer="91"/>
<wire x1="134.62" y1="2.54" x2="114.3" y2="2.54" width="0.1524" layer="91"/>
<pinref part="DM2B" gate="1" pin="A"/>
<wire x1="144.78" y1="33.02" x2="144.78" y2="25.4" width="0.1524" layer="91"/>
<wire x1="144.78" y1="25.4" x2="134.62" y2="25.4" width="0.1524" layer="91"/>
<junction x="134.62" y="25.4"/>
</segment>
</net>
<net name="N$13" class="0">
<segment>
<pinref part="M2A" gate="2" pin="S"/>
<wire x1="93.98" y1="66.04" x2="93.98" y2="53.34" width="0.1524" layer="91"/>
<pinref part="M2B" gate="2" pin="O"/>
<wire x1="132.08" y1="66.04" x2="132.08" y2="53.34" width="0.1524" layer="91"/>
<wire x1="132.08" y1="53.34" x2="93.98" y2="53.34" width="0.1524" layer="91"/>
<pinref part="JPM-PWM" gate="G$1" pin="1"/>
<wire x1="-12.7" y1="40.64" x2="2.54" y2="40.64" width="0.1524" layer="91"/>
<wire x1="2.54" y1="40.64" x2="2.54" y2="50.8" width="0.1524" layer="91"/>
<wire x1="2.54" y1="50.8" x2="93.98" y2="50.8" width="0.1524" layer="91"/>
<wire x1="93.98" y1="50.8" x2="93.98" y2="53.34" width="0.1524" layer="91"/>
<junction x="93.98" y="53.34"/>
</segment>
</net>
<net name="N$4" class="0">
<segment>
<pinref part="PWR_MAIN" gate="G$1" pin="2"/>
<wire x1="-17.78" y1="71.12" x2="-10.16" y2="71.12" width="0.1524" layer="91"/>
<wire x1="-10.16" y1="71.12" x2="-10.16" y2="66.04" width="0.1524" layer="91"/>
<wire x1="-10.16" y1="66.04" x2="-2.54" y2="66.04" width="0.1524" layer="91"/>
<pinref part="RMAIN" gate="2" pin="S"/>
<wire x1="-2.54" y1="66.04" x2="-2.54" y2="68.58" width="0.1524" layer="91"/>
</segment>
</net>
<net name="N$14" class="0">
<segment>
<pinref part="RMAIN" gate="1" pin="1"/>
<wire x1="2.54" y1="15.24" x2="2.54" y2="10.16" width="0.1524" layer="91"/>
<wire x1="2.54" y1="10.16" x2="-20.32" y2="10.16" width="0.1524" layer="91"/>
<wire x1="-20.32" y1="10.16" x2="-20.32" y2="58.42" width="0.1524" layer="91"/>
<wire x1="-20.32" y1="58.42" x2="-12.7" y2="58.42" width="0.1524" layer="91"/>
<wire x1="-12.7" y1="58.42" x2="-12.7" y2="68.58" width="0.1524" layer="91"/>
<pinref part="PWR_MAIN" gate="G$1" pin="1"/>
<wire x1="-12.7" y1="68.58" x2="-17.78" y2="68.58" width="0.1524" layer="91"/>
<pinref part="DRMAIN" gate="1" pin="A"/>
<wire x1="10.16" y1="20.32" x2="10.16" y2="10.16" width="0.1524" layer="91"/>
<wire x1="10.16" y1="10.16" x2="2.54" y2="10.16" width="0.1524" layer="91"/>
<junction x="2.54" y="10.16"/>
</segment>
</net>
</nets>
</sheet>
</sheets>
</schematic>
</drawing>
</eagle>
