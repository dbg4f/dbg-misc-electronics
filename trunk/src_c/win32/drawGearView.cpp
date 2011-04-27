// pdrawView.cpp : implementation of the CPdrawView class
//

#include "stdafx.h"
#include "pdraw.h"

#include "pdrawDoc.h"
#include "pdrawView.h"


#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CPdrawView

IMPLEMENT_DYNCREATE(CPdrawView, CView)

BEGIN_MESSAGE_MAP(CPdrawView, CView)
	//{{AFX_MSG_MAP(CPdrawView)
	ON_COMMAND(ID_BUTTON_REDRAW, OnButtonRedraw)
	//}}AFX_MSG_MAP
	// Standard printing commands
	ON_COMMAND(ID_FILE_PRINT, CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_DIRECT, CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_PREVIEW, CView::OnFilePrintPreview)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CPdrawView construction/destruction

CPdrawView::CPdrawView()
{
	// TODO: add construction code here

}

CPdrawView::~CPdrawView()
{
}

BOOL CPdrawView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return CView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// CPdrawView drawing


void drawHatch(CDC* pDC, int& x, int y, int width)
{
		pDC->MoveTo(x, y);
		pDC->LineTo(x, y + width);
		x+=100;	
}


void drawMeter(CDC* pDC, int x, int y)
{
	
	for (int i=0; i<20; i++)
	{
		
		drawHatch(pDC, x, y, 900);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 600);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);
		drawHatch(pDC, x, y, 300);

	}
}

#define PI (3.14159)

int round(double d)
{
	return (d - (int)d) > 0.5 ? int(d) : int(d) + 1;
}

void drawGear(CDC* pDC, int x, int y, int N, double w)
{
	
	int out2 = 2*100;
	
	int R = round((w*N*100.0/2.0)/PI);

	//pDC->Ellipse(x+R, y-R, x-R, y+R);

	pDC->Ellipse(x+R+out2, y-R-out2, x-R-out2, y+R+out2);
	
	pDC->Ellipse(x+R, y-R, x-R, y+R);
	
	int out = 2*100;

	int c = 3*100;

	for (int i=0; i<N; i++)
	{
		int y1 = round((R + out)*sin(2.0*PI*i/N));
		int x1 = round((R + out)*cos(2.0*PI*i/N));

		pDC->MoveTo(x, y);
		pDC->LineTo(x + x1, y + y1);
	}

	pDC->Ellipse(x+c, y-c, x-c, y+c);

	pDC->MoveTo(x, y-c);
	pDC->LineTo(x, y+c);

	pDC->MoveTo(x+c, y);
	pDC->LineTo(x-c, y);

	//pDC->Ellipse(x+R+out2, y-R-out2, x-R-out2, y+R+out2);
}


void drawRawHatch(CDC* pDC, int x, int y, int count, int w)
{
	for (int i=0; i<count; i++)
	{
		pDC->MoveTo(x + i*w, y);
		pDC->LineTo(x + i*w, y + 5*100);
	}
}

/*
#define MODE MM_TEXT
#define SX 1
#define SY 1
*/

#define MODE MM_HIMETRIC
#define SX 100
#define SY (-100)



void inv(CDC* pDC, long cx, long cy, long r, double a, double a2, long direction)
{
	int steps = 100;
	long x1, x2, y1, y2;
	
	double a1 = 0.0;
	//double a2 = 2.0*PI/5.0 + 0.1;

	double t = 0.0;

	double dt = (a2 - a1)/steps;

	for (int i=0; i<steps+1; i++, t+=dt)
	{
				
		x2 = round(r * (cos(t - a) + t * sin(t - a)));
		y2 = round(r * (sin(t - a) - t * cos(t - a)));

		y2 *= direction;

		if (i>0)
		{
			pDC->MoveTo(x1 + cx, y1 + cy);
			pDC->LineTo(x2 + cx, y2 + cy);
		}
				
		x1 = x2;
		y1 = y2;

	}
		
}


double invv(double a)
{
	return (sin(a)/cos(a) - a);
}

void gearWheel(CDC* pDC, CGearWheel w)
{

	double p = (2.0 * PI / (double)w.z);
	double e = (1.0 - w.ps) * p;
	double s =  w.ps * p;
	long rc = 300;

	//pDC->Ellipse(w.cx-w.r-w.ab, w.cy-w.r-w.ab, w.cx+w.r+w.ab, w.cy+w.r+w.ab);
	
	pDC->Ellipse(w.cx-w.r, w.cy-w.r, w.cx+w.r, w.cy+w.r);
	pDC->Ellipse(w.cx-rc, w.cy-rc, w.cx+rc, w.cy+rc);
	pDC->MoveTo(w.cx-rc, w.cy);
	pDC->LineTo(w.cx+rc, w.cy);
	pDC->MoveTo(w.cx, w.cy-rc);
	pDC->LineTo(w.cx, w.cy+rc);

	
	double ei = w.startAngle;
	double si = -w.startAngle + s;

	double invAngle = acos((double)w.r/(double)(w.r + w.ab));

	double invAngleTo = invAngle + invv(invAngle);

	double alphaA = s - 2.0 * invv(invAngle);

	for (int i=0; i<w.z; i++, ei+=p, si+=p)
	{
		inv(pDC, w.cx, w.cy, w.r, ei, invAngleTo,  1);
				
		inv(pDC, w.cx, w.cy, w.r, si, invAngleTo, -1);	

		int ra = round(w.r + w.ab);

		double angleAShift = (s - alphaA)/2.0;

		int xx1 = w.cx+round((double)ra*cos(si - angleAShift));
		int yy1 = w.cy+round((double)ra*sin(si - angleAShift));
		int xx2 = w.cx+round((double)ra*cos(si - s + angleAShift));
		int yy2 = w.cy+round((double)ra*sin(si - s + angleAShift));

		pDC->Arc(w.cx-ra, w.cy-ra, w.cx+ra, w.cy+ra, xx2, yy2, xx1, yy1);

	}

	
	char txt[1000] = "AAA";

	CString st(txt);	

	pDC->TextOut(5000, -1000, txt);

}

double addA = 0.0;

void CPdrawView::OnDraw(CDC* pDC)
{
	CPdrawDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	// TODO: add draw code for native data here

	pDC->SetMapMode(MODE);



	//pDC->Ellipse(3000, -3000, 7000, -7000);
	
	
	//drawMeter(pDC, 4000, -4000); 


	CGearWheel w1;
	
	w1.cx			= 6000;
	w1.cy			= -6000;
	w1.r			= 1000;
	w1.ab			= 500;
	w1.ps			= 0.8;
	w1.z			= 8;
	w1.startAngle	= addA;

	gearWheel(pDC, w1);


	CGearWheel w2;
	/*
	w2.cx			= 13500;
	w2.cy			= -6000;
	w2.r			= 3000;
	w2.ra			= 3000;
	w2.ab			= 1100;
	w2.ps			= 0.85;
	w2.z			= 12;
	w2.startAngle	= -addA + 0.2;
	*/

	w2.cx			= w1.cx + w1.r + 600 + 3000 + 1000;
	w2.cy			= -6000;
	w2.r			= 3000;
	w2.ab			= 600;
	w2.ps			= 0.7;
	w2.z			= 24;
	w2.startAngle	= -addA / 3.0 + 0.07;


	gearWheel(pDC, w2);

	addA += 0.03;

}

/////////////////////////////////////////////////////////////////////////////
// CPdrawView printing

BOOL CPdrawView::OnPreparePrinting(CPrintInfo* pInfo)
{
	// default preparation
	return DoPreparePrinting(pInfo);
}

void CPdrawView::OnBeginPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: add extra initialization before printing
}

void CPdrawView::OnEndPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: add cleanup after printing
}

/////////////////////////////////////////////////////////////////////////////
// CPdrawView diagnostics

#ifdef _DEBUG
void CPdrawView::AssertValid() const
{
	CView::AssertValid();
}

void CPdrawView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

CPdrawDoc* CPdrawView::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CPdrawDoc)));
	return (CPdrawDoc*)m_pDocument;
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CPdrawView message handlers

void CPdrawView::OnButtonRedraw() 
{
	// TODO: Add your command handler code here
	//AfxMessageBox("AAA");

	this->Invalidate();

}
