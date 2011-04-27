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
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
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


#define SCALEX 100
#define SCALEY (-100)

void drawCm(CDC* pDC, int startX, int startY, int width, int& shiftX)
{
	pDC->MoveTo((startX + shiftX)*SCALEX,	startY			*SCALEY);
	pDC->LineTo((startX + shiftX)*SCALEX,	(startY + width)*SCALEY);
	shiftX++;
}

void drawMX(CDC* pDC, int startX, int startY, int cm)
{
	int l=3;

	int mm1 = l;
	int mm5 = 2*l;
	int mm10 = 3*l;

	int currentX = 0;

	for (int i=0; i<cm; i++)
	{
		drawCm(pDC, startX, startY, mm10,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm5,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
		drawCm(pDC, startX, startY, mm1,	currentX);
	}
}


int round(double d)
{
	int x = (int)d;

	double r = d - (double)x;

	return r > 0.5 ? x+1 : x;

}

void drawGear(CDC* pDC, int cx, int cy, int N, double w)
{

	
	//pDC->Ellipse(10*SCALEX,  50*SCALEY,  30*SCALEX,  70*SCALEY);

	//pDC->Rectangle( 10*SCALEX, 50*SCALEY,  30*SCALEX, 70*SCALEY);


	int out = 50*100;

	double RR = ((N*w*100.0)/3.14)/2.0;

	int R = round (RR);

	pDC->Ellipse(cx-R, cy+R, cx+R, cy-R);

	pDC->MoveTo(cx, cy);
	pDC->LineTo(cx, cy + R + out);

	
	for (int i=1; i<N; i++)
	{

		double x1 = (R + out) * sin(i*(2*3.14/(double)N));
		double y1 = (R + out) * cos(i*(2*3.14/(double)N));

		pDC->MoveTo(cx,				cy);	
		pDC->LineTo(cx + round(x1), cy + round(y1));
	}


}

void CPdrawView::OnDraw(CDC* pDC)
{
	CPdrawDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	// TODO: add draw code for native data here

	int dc = pDC->SaveDC();

	pDC->SetMapMode(MM_HIMETRIC); // 0.01 mm

	pDC->LineTo(1000, -1000);

	
	//CString st("AAAA");

	//pDC->TextOut(1000, -1000, st);

	drawMX(pDC, 30, 30, 10);


	drawGear(pDC, 7000, -7000, 50, 2.6);

	pDC->RestoreDC(dc);

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
