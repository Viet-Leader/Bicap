<template>
<div class="container-fluid py-2">
      <div class="row mb-4">
        <div class="col-12">
          <h3 class="mb-0 h4 font-weight-bolder">Dashboard</h3>
          <p class="mb-4">
            Check the sales, value and bounce rate by country.
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-xl-3 col-sm-6 mb-xl-0 mb-4 fadeInUp">
  <div class="card" style="border-radius: 15px;">
    <div class="card-header p-3 ps-3" style="background: linear-gradient(135deg, #43A047 0%, #2E7D32 100%); border-radius: 15px 15px 0 0;">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <p class="text-sm mb-0 text-white text-capitalize opacity-8">Total Revenue</p>
          <h4 class="mb-0 text-white font-weight-bolder">150.000.000đ</h4> 
        </div>
        <div class="icon icon-md icon-shape bg-white shadow text-center border-radius-lg" style="background: rgba(255,255,255,0.2) !important;">
          <i class="material-symbols-rounded text-white opacity-10">payments</i>
        </div>
      </div>
    </div>
    <hr class="dark horizontal my-0">
    <div class="card-footer p-3 ps-3" style="background: #f8f9fa; border-radius: 0 0 15px 15px;">
      <p class="mb-0 text-sm"><span class="text-success font-weight-bolder">+10% </span> compared to last month</p>
    </div>
  </div>
</div>

<div class="col-xl-3 col-sm-6 mb-xl-0 mb-4 fadeInUp" style="animation-delay: 0.1s;">
  <div class="card" style="border-radius: 15px;">
    <div class="card-header p-3 ps-3" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 15px 15px 0 0;">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <p class="text-sm mb-0 text-white text-capitalize opacity-8">Current Season</p>
          <h4 class="mb-0 text-white font-weight-bolder">3 Types</h4>
        </div>
        <div class="icon icon-md icon-shape bg-white shadow text-center border-radius-lg" style="background: rgba(255,255,255,0.2) !important;">
          <i class="material-symbols-rounded text-white opacity-10">grass</i> 
        </div>
      </div>
    </div>
    <hr class="dark horizontal my-0">
    <div class="card-footer p-3 ps-3" style="background: #f8f9fa; border-radius: 0 0 15px 15px;">
      <p class="mb-0 text-sm">Rice, Corn, Tomato</p>
    </div>
  </div>
</div>

<div class="col-xl-3 col-sm-6 mb-xl-0 mb-4 fadeInUp" style="animation-delay: 0.2s;">
  <div class="card" style="border-radius: 15px;">
    <div class="card-header p-3 ps-3" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius: 15px 15px 0 0;">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <p class="text-sm mb-0 text-white text-capitalize opacity-8">Orders in Transit</p>
          <h4 class="mb-0 text-white font-weight-bolder">5 Orders</h4>
        </div>
        <div class="icon icon-md icon-shape bg-white shadow text-center border-radius-lg" style="background: rgba(255,255,255,0.2) !important;">
          <i class="material-symbols-rounded text-white opacity-10">local_shipping</i>
        </div>
      </div>
    </div>
    <hr class="dark horizontal my-0">
    <div class="card-footer p-3 ps-3" style="background: #f8f9fa; border-radius: 0 0 15px 15px;">
      <p class="mb-0 text-sm">Expected to complete today</p>
    </div>
  </div>
</div>

      <div class="col-xl-3 col-sm-6 fadeInUp" style="animation-delay: 0.3s;">
        <div class="card" style="border-radius: 15px;">
          <div class="card-header p-3 ps-3" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); border-radius: 15px 15px 0 0;">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <p class="text-sm mb-0 text-white text-capitalize opacity-8">Agricultural Stock</p>
                <h4 class="mb-0 text-white font-weight-bolder">2.5 Tons</h4>
              </div>
              <div class="icon icon-md icon-shape bg-white shadow text-center border-radius-lg" style="background: rgba(255,255,255,0.2) !important;">
                <i class="material-symbols-rounded text-white opacity-10">warehouse</i>
              </div>
            </div>
          </div>
          <hr class="dark horizontal my-0">
          <div class="card-footer p-3 ps-3" style="background: #f8f9fa; border-radius: 0 0 15px 15px;">
            <p class="mb-0 text-sm"><span class="text-success font-weight-bolder">+5% </span> new stock added yesterday</p>
          </div>
        </div>
      </div>  
      <div class="col-lg-4 col-md-6 mt-4 mb-4">
        <div class="card">
          <div class="card-body">
            <h6 class="mb-0">Harvest Yield</h6>
            <p class="text-sm">Recent season productivity</p>
            <div class="pe-2">
              <div class="chart">
                <canvas id="chart-bars" class="chart-canvas" height="170"></canvas>
              </div>
            </div>
            <hr class="dark horizontal">
            <div class="d-flex">
              <i class="material-symbols-rounded text-sm my-auto me-1">schedule</i>
              <p class="mb-0 text-sm"> Updated 2 days ago </p>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4 col-md-6 mt-4 mb-4">
        <div class="card ">
          <div class="card-body">
            <h6 class="mb-0">Market Price Fluctuations</h6>
            <p class="text-sm"> (<span class="font-weight-bolder text-success">+15%</span>) today's coffee price. </p>
            <div class="pe-2">
              <div class="chart">
                <canvas id="chart-line" class="chart-canvas" height="170"></canvas>
              </div>
            </div>
            <hr class="dark horizontal">
            <div class="d-flex ">
              <i class="material-symbols-rounded text-sm my-auto me-1">schedule</i>
              <p class="mb-0 text-sm"> Updated 4 minutes ago </p>
            </div>
          </div>
        </div>
      </div>

        <div class="col-lg-4 mt-4 mb-3">
          <div class="card">
            <div class="card-body">
              <h6 class="mb-0">Work Progress</h6>
              <p class="text-sm">Irrigation, fertilization, harvesting...</p>
              <div class="pe-2">
                <div class="chart">
                  <canvas id="chart-line-tasks" class="chart-canvas" height="170"></canvas>
                </div>
              </div>
              <hr class="dark horizontal">
              <div class="d-flex ">
                <i class="material-symbols-rounded text-sm my-auto me-1">schedule</i>
                <p class="mb-0 text-sm">Just updated</p>
              </div>
            </div>
          </div>
        </div>          
        
        
      </div>
      <div class="row mb-4">
        <div class="col-lg-8 col-md-6 mb-md-0 mb-4">
          <div class="card">
            <div class="card-header pb-0">
              <div class="row">
                <div class="col-lg-6 col-7">
                  <h6>Agricultural Warehouse & Status</h6>
                  <p class="text-sm mb-0">
                    <i class="fa fa-check text-info" aria-hidden="true"></i>
                    <span class="font-weight-bold ms-1">30 tons</span> shipped this month
                  </p>
                </div>
              </div>
            </div>
            <div class="card-body p-3">
              <div class="table-responsive p-0">
                <table class="table align-items-center mb-0">
                  <thead>
                    <tr>
                      <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Product Name</th>
                      <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Customer/Partner</th>
                      <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Unit Value</th>
                      <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">Warehouse Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <div class="d-flex px-2 py-1">
                          <div>
                            <img src="/admin-assets/img/small-logos/logo-xd.svg" class="avatar avatar-sm me-3" alt="xd"> 
                          </div>
                          <div class="d-flex flex-column justify-content-center">
                            <h6 class="mb-0 text-sm">Organic Tomato</h6> <p class="text-xs text-secondary mb-0">Lot #A102</p>
                          </div>
                        </div>
                      </td>
                      <td>
                        <div class="avatar-group mt-2">
                          <span class="text-xs font-weight-bold">BigC Supermarket</span>
                        </div>
                      </td>
                      <td class="align-middle text-center text-sm">
                        <span class="text-xs font-weight-bold"> 50,000,000 VND </span>
                      </td>
                      <td class="align-middle">
                        <div class="progress-wrapper w-75 mx-auto">
                          <div class="progress-info">
                            <div class="progress-percentage">
                              <span class="text-xs font-weight-bold">Sold 60%</span>
                            </div>
                          </div>
                          <div class="progress">
                            <div class="progress-bar bg-gradient-info w-60" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"></div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-4 col-md-6">
          <div class="card h-100">
  <div class="card-header pb-0">
    <h6>Activity Log</h6>
    <p class="text-sm">
      <i class="fa fa-arrow-up text-success" aria-hidden="true"></i>
      <span class="font-weight-bold">24</span> new activities this week
    </p>
  </div>
  <div class="card-body p-3">
    <div class="timeline timeline-one-side">
      
      <div class="timeline-block mb-3">
        <span class="timeline-step">
          <i class="material-symbols-rounded text-success text-gradient">grass</i>
        </span>
        <div class="timeline-content">
          <h6 class="text-dark text-sm font-weight-bold mb-0">Harvested Corn Lot #05</h6>
          <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">22 DEC 7:20 PM</p>
        </div>
      </div>

      <div class="timeline-block mb-3">
        <span class="timeline-step">
          <i class="material-symbols-rounded text-danger text-gradient">water_drop</i>
        </span>
        <div class="timeline-content">
          <h6 class="text-dark text-sm font-weight-bold mb-0">Warning: Low Soil Moisture</h6>
          <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">21 DEC 11 PM</p>
        </div>
      </div>

      <div class="timeline-block mb-3">
        <span class="timeline-step">
          <i class="material-symbols-rounded text-info text-gradient">local_shipping</i>
        </span>
        <div class="timeline-content">
          <h6 class="text-dark text-sm font-weight-bold mb-0">Order #1832412 is in transit</h6>
          <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">21 DEC 9:34 PM</p>
        </div>
      </div>

       <div class="timeline-block mb-3">
        <span class="timeline-step">
          <i class="material-symbols-rounded text-warning text-gradient">science</i>
        </span>
        <div class="timeline-content">
          <h6 class="text-dark text-sm font-weight-bold mb-0">Fertilized Area B</h6>
          <p class="text-secondary font-weight-bold text-xs mt-1 mb-0">20 DEC 2:20 AM</p>
        </div>
      </div>

    </div>
  </div>
</div>
          </div>
        </div>
      </div>
      <footer class="footer py-4  ">
        <div class="container-fluid">
          <div class="row align-items-center justify-content-lg-between">
            <div class="col-lg-6 mb-lg-0 mb-4">
              <div class="copyright text-center text-sm text-muted text-lg-start">
                © ,
                made with <i class="fa fa-heart"></i> by
                <a href="https://www.creative-tim.com" class="font-weight-bold" target="_blank">BiCap</a>
                for a better web.
              </div>
            </div>
            <div class="col-lg-6">
              <ul class="nav nav-footer justify-content-center justify-content-lg-end">
                <li class="nav-item">
                  <a href="https://www.creative-tim.com" class="nav-link text-muted" target="_blank">Creative Tim</a>
                </li>
                <li class="nav-item">
                  <a href="https://www.creative-tim.com/presentation" class="nav-link text-muted" target="_blank">About Us</a>
                </li>
                <li class="nav-item">
                  <a href="https://www.creative-tim.com/blog" class="nav-link text-muted" target="_blank">Blog</a>
                </li>
                <li class="nav-item">
                  <a href="https://www.creative-tim.com/license" class="nav-link pe-0 text-muted" target="_blank">License</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </footer>
  
</template>

<script setup>
import { onMounted } from 'vue';

onMounted(() => {
  // Logic from dashboard.ejs
});
</script>

<style scoped>
/* Scoped styles */
</style>
